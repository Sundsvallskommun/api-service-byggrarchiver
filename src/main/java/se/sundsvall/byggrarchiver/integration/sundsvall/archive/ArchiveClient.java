package se.sundsvall.byggrarchiver.integration.sundsvall.archive;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import se.sundsvall.byggrarchiver.integration.sundsvall.archive.configuration.ArchiveConfiguration;

import generated.se.sundsvall.archive.ArchiveResponse;
import generated.se.sundsvall.archive.ByggRArchiveRequest;

@FeignClient(name = "archive", url = "${integration.archive.url}", configuration = ArchiveConfiguration.class)
public interface ArchiveClient {

    @PostMapping(path = "/archive/byggr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ArchiveResponse postArchive(@RequestBody ByggRArchiveRequest archiveMessage);

}
