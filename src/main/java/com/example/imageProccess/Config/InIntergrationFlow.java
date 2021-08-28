package com.example.imageProccess.Config;

import com.example.imageProccess.service.ImageResizer;
import org.ietf.jgss.ChannelBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Transformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.integration.zip.transformer.UnZipTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;
import org.springframework.messaging.Message;

import java.io.File;
import java.util.Set;
import java.util.zip.Deflater;

@Configuration
public class InIntergrationFlow {
    @Bean
    public UnZipTransformer unZipTransformer(){
        UnZipTransformer unZipTransformer = new UnZipTransformer();
        unZipTransformer.setExpectSingleResult(true);
        unZipTransformer.setZipResultType(ZipResultType.FILE);
        unZipTransformer.setWorkDirectory(new File("/tmp"));
        unZipTransformer.setDeleteFiles(true);
        return unZipTransformer;
    }
    @Bean
    public ZipTransformer zipTransformer() {
        ZipTransformer zipTransformer = new ZipTransformer();
        zipTransformer.setCompressionLevel(Deflater.BEST_COMPRESSION);
        zipTransformer.setZipResultType(ZipResultType.BYTE_ARRAY);
        zipTransformer.setDeleteFiles(true);
        return zipTransformer;
    }
    @Bean
    IntegrationFlow ImageputFile(@Value("${input-directory:${HOME}/Desktop/ImageProcess/in}") File in,
                              @Value("${input-directory:${HOME}/Desktop/out}") File out,
                              UnZipTransformer unZipTransformer,
                              ImageResizer imageResizer,
                                 ZipTransformer zipTransformer){
        GenericTransformer<File, Set<File>> transformer =  ( source) ->
        {
            return   imageResizer.resizeImage((source),"/tmp");

        };


        return IntegrationFlows
                .from(Files.inboundAdapter(in)
                                .autoCreateDirectory(true)
                                .preventDuplicates(true),
                        poller -> poller.poller(pm -> pm.fixedRate(1000)))
//                .transform(File.class)
                .transform(unZipTransformer)
                .transform(transformer)

//                //.handle(imageResizer.resizeImage(in, in.getPath()); i think this more practise
               .transform(zipTransformer)
                .handle(Files.outboundAdapter(out)
                        .autoCreateDirectory(true)
                        .fileNameGenerator(
                                message ->{
                                    System.out.println(message);
//                                    System.out.println(message.getHeaders().get("AbsolutePath"));
//                                    new File((String) message.getHeaders().get("AbsolutePath")).delete();
                                    return( "sfsadf")
                                            +"_" +
                                            ( (String)message.getHeaders().get(FileHeaders.FILENAME))
                                                    .split("\\.")[0] +
                                            ".zip";
                                }
                        ))
                .get();


    }
}
