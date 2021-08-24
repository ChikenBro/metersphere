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

-- 缺陷趋势表
CREATE TABLE issue_trend  (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `project_id` varchar(50)   NOT NULL COMMENT 'Project ID',
    `jira_key` varchar(50)  NOT NULL COMMENT 'Jira Project Key',
    `issue_week` INT NOT NULL default 0 COMMENT '本年第几周',
    `start_week_time` varchar(50)  NOT NULL COMMENT '本周开始时间',
    `end_week_time` varchar(50)  NOT NULL COMMENT '本周结束时间',
    `issue_total` INT NOT NULL default 0 COMMENT '本周新增bug',
    `resolution_issue_total` INT NOT NULL default 0 COMMENT '本周解决bug总数',
    `resolution_week_issue` INT NOT NULL default 0 COMMENT '本周解决新增bug',
    `resolution_history_issue` INT NOT NULL default 0 COMMENT '本周解决历史bug',
    `test_env_issue` INT NOT NULL default 0 COMMENT '测试环境新增bug',
    `uat_env_issue` INT NOT NULL default 0 COMMENT 'UAT环境新增bug',
    `prod_env_issue` INT NOT NULL default 0 COMMENT 'PROD环境新增bug',
    `create_time` bigint(13) NOT NULL COMMENT 'Create timestamp',
    `update_time` bigint(13) NOT NULL COMMENT 'Update timestamp',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


