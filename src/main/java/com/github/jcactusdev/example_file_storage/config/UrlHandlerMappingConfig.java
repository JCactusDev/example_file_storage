package com.github.jcactusdev.example_file_storage.config;

import com.github.jcactusdev.example_file_storage.controller.DownloadController;
import com.github.jcactusdev.example_file_storage.storage.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class UrlHandlerMappingConfig {

    @Autowired
    public void registerOverriddenControllerEndpoint(
            final RequestMappingHandlerMapping handlerMapping,
            final DownloadController controller,
            @Qualifier("fileStorageProperties") final FileStorageProperties properties
    ) throws NoSuchMethodException {
        final RequestMappingInfo mapping = RequestMappingInfo.paths(String.format("/%s/{directory}/{fileName:.+}", properties.getUrlDownload()))
                .methods(RequestMethod.GET)
                .build();
        handlerMapping.unregisterMapping(mapping);
        handlerMapping.registerMapping(mapping, controller, DownloadController.class.getMethod("getFile", String.class, String.class));
    }

}
