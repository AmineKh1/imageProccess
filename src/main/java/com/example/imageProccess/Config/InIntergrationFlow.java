package com.example.imageProccess.Config;

import com.example.imageProccess.service.ImageResizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.zip.transformer.UnZipTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;

import java.io.File;
import java.util.zip.Deflater;

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
        return zipTransformer;
    }
    @Bean
    IntegrationFlow ImageputFile(@Value("${input-directory:${HOME}/Desktop/ImageProcess/in}") File in,
                              @Value("${input-directory:${HOME}/Desktop/out}") File out,
                              UnZipTransformer unZipTransformer, Environment environment,
                              ImageResizer imageResizer, ZipTransformer zipTransformer){

        return IntegrationFlows
                .from(Files.inboundAdapter(in)
                                .autoCreateDirectory(true)
                                .preventDuplicates(true),
                        poller -> poller.poller(pm -> pm.fixedRate(1000)))
                .transform(File.class)
                .transform(unZipTransformer)
                .split()
                .handle(message -> {
                        imageResizer.resizeImage(in, in.getPath());message.getPayload();// send event through fileStatus channel
                })
                .transform(zipTransformer)
                .handle(Files.outboundAdapter(out)
                        .autoCreateDirectory(true)
                        .fileNameGenerator(
                                message ->{
                                    System.out.println(message);
                                    System.out.println(message.getHeaders().get("AbsolutePath"));
                                    new File((String) message.getHeaders().get("AbsolutePath")).delete();
                                    return( (String)message.getHeaders().get("realFileName")).split("\\.")[0]
                                            +"_" +
                                            ( (String)message.getHeaders().get(FileHeaders.FILENAME)).split("\\.")[0] +
                                            ".zip";
                                }
                        ))
                .get();


    }
}
