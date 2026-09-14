-- 1. Départements

CREATE TABLE `departments` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `description` varchar(255) DEFAULT NULL,
                               `name` varchar(255) NOT NULL,

                               PRIMARY KEY (`id`),
                               UNIQUE KEY `UKj6cwks7xecs5jov19ro8ge3qk` (`name`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 2. Utilisateurs

CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `email` varchar(255) NOT NULL,
                         `first_name` varchar(255) NOT NULL,
                         `last_name` varchar(255) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `role` enum(
        'ADMIN',
        'QHSE_MANAGER',
        'QUALITY_MANAGER',
        'STAFF'
    ) NOT NULL,
                         `department_id` bigint DEFAULT NULL,
                         `username` varchar(255) NOT NULL,

                         PRIMARY KEY (`id`),

                         UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
                         UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),

                         KEY `FKsbg59w8q63i0oo53rlgvlcnjq` (`department_id`),

                         CONSTRAINT `FKsbg59w8q63i0oo53rlgvlcnjq`
                             FOREIGN KEY (`department_id`)
                                 REFERENCES `departments` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 3. Incidents

CREATE TABLE `incidents` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `created_at` datetime(6) NOT NULL,
                             `description` varchar(3000) NOT NULL,
                             `status` enum(
        'CLOSED',
        'IN_PROGRESS',
        'OPEN',
        'RESOLVED'
    ) NOT NULL,
                             `title` varchar(255) NOT NULL,
                             `department_id` bigint NOT NULL,
                             `reporter_id` bigint NOT NULL,
                             `gravity` enum(
        'CRITICAL',
        'HIGH',
        'LOW',
        'MEDIUM'
    ) NOT NULL,
                             `incident_date` date NOT NULL,
                             `type` enum(
        'EQUIPMENT',
        'HYGIENE',
        'MEDICAL',
        'MEDICATION',
        'ORGANIZATIONAL',
        'OTHER',
        'SECURITY'
    ) NOT NULL,

                             PRIMARY KEY (`id`),

                             KEY `FK2ocagapgc95lh9o38klhx0t2f` (`department_id`),
                             KEY `FKofcijy0t7vqdfsq7o5nmk59w1` (`reporter_id`),

                             CONSTRAINT `FK2ocagapgc95lh9o38klhx0t2f`
                                 FOREIGN KEY (`department_id`)
                                     REFERENCES `departments` (`id`),

                             CONSTRAINT `FKofcijy0t7vqdfsq7o5nmk59w1`
                                 FOREIGN KEY (`reporter_id`)
                                     REFERENCES `users` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 4. Audits

CREATE TABLE `audits` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `audit_date` date NOT NULL,
                          `conformity_rate` double NOT NULL,
                          `observations` varchar(255) DEFAULT NULL,
                          `score` int NOT NULL,
                          `title` varchar(255) NOT NULL,
                          `department_id` bigint DEFAULT NULL,
                          `compliant_criteria` int NOT NULL,
                          `total_criteria` int DEFAULT NULL,

                          PRIMARY KEY (`id`),

                          KEY `FKsn2hcfg2hler6hsmcu66on5pj` (`department_id`),

                          CONSTRAINT `FKsn2hcfg2hler6hsmcu66on5pj`
                              FOREIGN KEY (`department_id`)
                                  REFERENCES `departments` (`id`),

                          CONSTRAINT `audits_chk_1`
                              CHECK (`compliant_criteria` >= 0)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 5. Actions correctives

CREATE TABLE `corrective_action` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `deadline` date NOT NULL,
                                     `description` varchar(255) NOT NULL,
                                     `status` enum(
        'CANCELLED',
        'COMPLETED',
        'IN_PROGRESS',
        'OVERDUE',
        'TODO'
    ) NOT NULL,
                                     `title` varchar(255) NOT NULL,
                                     `incident_id` bigint NOT NULL,
                                     `responsible_user_id` bigint NOT NULL,

                                     PRIMARY KEY (`id`),

                                     KEY `FKrd5honqvev6bfvc1cvvpkgknb` (`incident_id`),
                                     KEY `FKhvyn8jrgp2og3s4ch7n4tbksm` (`responsible_user_id`),

                                     CONSTRAINT `FKhvyn8jrgp2og3s4ch7n4tbksm`
                                         FOREIGN KEY (`responsible_user_id`)
                                             REFERENCES `users` (`id`),

                                     CONSTRAINT `FKrd5honqvev6bfvc1cvvpkgknb`
                                         FOREIGN KEY (`incident_id`)
                                             REFERENCES `incidents` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 6. Admissions mensuelles

CREATE TABLE `monthly_admissions` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `admission_count` int NOT NULL,
                                      `admission_month` int NOT NULL,
                                      `admission_year` int NOT NULL,
                                      `department_id` bigint NOT NULL,

                                      PRIMARY KEY (`id`),

                                      UNIQUE KEY `UK38orm4extolal67tiw137s6u3` (
                                          `department_id`,
                                          `admission_year`,
                                          `admission_month`
                                          ),

                                      CONSTRAINT `FKh93drcmigu4jp8nspcuuw5f88`
                                          FOREIGN KEY (`department_id`)
                                              REFERENCES `departments` (`id`),

                                      CONSTRAINT `monthly_admissions_chk_1`
                                          CHECK (`admission_count` >= 0),

                                      CONSTRAINT `monthly_admissions_chk_2`
                                          CHECK (`admission_month` >= 1 AND `admission_month` <= 12),

                                      CONSTRAINT `monthly_admissions_chk_3`
                                          CHECK (`admission_year` >= 1)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 7. Indicateurs qualité

CREATE TABLE `quality_indicator` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `name` varchar(255) NOT NULL,
                                     `period` varchar(255) NOT NULL,
                                     `target_value` int NOT NULL,
                                     `value` int NOT NULL,
                                     `department_id` bigint DEFAULT NULL,

                                     PRIMARY KEY (`id`),

                                     KEY `FKgbhnixwx3aws4f1be77qhl387` (`department_id`),

                                     CONSTRAINT `FKgbhnixwx3aws4f1be77qhl387`
                                         FOREIGN KEY (`department_id`)
                                             REFERENCES `departments` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- 8. Rapports

CREATE TABLE `report` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `created_at` date NOT NULL,
                          `file_url` varchar(255) NOT NULL,
                          `title` varchar(255) NOT NULL,
                          `type` enum(
        'AUDIT_PDF',
        'INCIDENT_EXCEL',
        'INCIDENT_PDF',
        'MONTHLY_QUALITY',
        'QHSE_DEPARTMENT'
    ) NOT NULL,
                          `department_id` bigint DEFAULT NULL,

                          PRIMARY KEY (`id`),

                          KEY `FKdkgxfol6uqi0lb5y7gnxypt87` (`department_id`),

                          CONSTRAINT `FKdkgxfol6uqi0lb5y7gnxypt87`
                              FOREIGN KEY (`department_id`)
                                  REFERENCES `departments` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;