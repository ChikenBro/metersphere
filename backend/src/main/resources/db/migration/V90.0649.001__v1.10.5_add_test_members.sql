-- 测试计划增加测试人员资源表
CREATE TABLE test_plan_members  (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `test_plan_id` varchar(50)   NOT NULL COMMENT 'Test plan ID',
    `project_id` varchar(50)   NOT NULL COMMENT 'Project ID',
    `tester` varchar(50)  NOT NULL COMMENT 'Test Member',
    `creator` varchar(50)  NOT NULL COMMENT 'Test creator',
    `test_case_total` int(11) NOT NULL default 0 COMMENT 'Test Case Total',
    `execute_test_case` int(11) NOT NULL default 0 COMMENT 'Execute Test Case',
    `test_case_pass` INT NOT NULL  default 0 COMMENT 'Test Case Pass',
    `test_case_fail` INT NOT NULL  default 0 COMMENT 'Test Case Fail',
    `test_case_block` INT NOT NULL  default 0 COMMENT 'Test Case Block',
    `ext_content` text DEFAULT NULL COMMENT 'content',
    `create_time` bigint(13) NOT NULL COMMENT 'Create timestamp',
    `update_time` bigint(13) NOT NULL COMMENT 'Update timestamp',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
