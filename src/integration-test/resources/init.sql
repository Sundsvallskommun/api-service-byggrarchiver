-- archiver.batch_history definition
CREATE TABLE `batch_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `batch_trigger` varchar(255) NOT NULL,
    `end` date NOT NULL,
    `start` date NOT NULL,
    `archive_status` varchar(255) NOT NULL,
    `timestamp` datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=latin1;

-- archiver.archive_history definition
CREATE TABLE `archive_history` (
    `case_id` varchar(255) NOT NULL,
    `document_id` varchar(255) NOT NULL,
    `archive_id` varchar(255) DEFAULT NULL,
    `archive_url` varchar(1000) DEFAULT NULL,
    `document_name` varchar(255) DEFAULT NULL,
    `document_type` varchar(255) DEFAULT NULL,
    `archive_status` varchar(255) NOT NULL,
    `timestamp` datetime(6) NOT NULL,
    `batch_history_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`case_id`,`document_id`),
    KEY `FK4255lb0tf1xmgpyb3lq341atk` (`batch_history_id`),
    CONSTRAINT `FK4255lb0tf1xmgpyb3lq341atk` FOREIGN KEY (`batch_history_id`) REFERENCES `batch_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
