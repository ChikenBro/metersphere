package io.metersphere.base.domain;

import java.util.ArrayList;
import java.util.List;

public class TestPlanMembersExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TestPlanMembersExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdIsNull() {
            addCriterion("test_plan_id is null");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdIsNotNull() {
            addCriterion("test_plan_id is not null");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdEqualTo(String value) {
            addCriterion("test_plan_id =", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdNotEqualTo(String value) {
            addCriterion("test_plan_id <>", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdGreaterThan(String value) {
            addCriterion("test_plan_id >", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdGreaterThanOrEqualTo(String value) {
            addCriterion("test_plan_id >=", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdLessThan(String value) {
            addCriterion("test_plan_id <", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdLessThanOrEqualTo(String value) {
            addCriterion("test_plan_id <=", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdLike(String value) {
            addCriterion("test_plan_id like", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdNotLike(String value) {
            addCriterion("test_plan_id not like", value, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdIn(List<String> values) {
            addCriterion("test_plan_id in", values, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdNotIn(List<String> values) {
            addCriterion("test_plan_id not in", values, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdBetween(String value1, String value2) {
            addCriterion("test_plan_id between", value1, value2, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andTestPlanIdNotBetween(String value1, String value2) {
            addCriterion("test_plan_id not between", value1, value2, "testPlanId");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNull() {
            addCriterion("project_id is null");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNotNull() {
            addCriterion("project_id is not null");
            return (Criteria) this;
        }

        public Criteria andProjectIdEqualTo(String value) {
            addCriterion("project_id =", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotEqualTo(String value) {
            addCriterion("project_id <>", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThan(String value) {
            addCriterion("project_id >", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThanOrEqualTo(String value) {
            addCriterion("project_id >=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThan(String value) {
            addCriterion("project_id <", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThanOrEqualTo(String value) {
            addCriterion("project_id <=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLike(String value) {
            addCriterion("project_id like", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotLike(String value) {
            addCriterion("project_id not like", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdIn(List<String> values) {
            addCriterion("project_id in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotIn(List<String> values) {
            addCriterion("project_id not in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdBetween(String value1, String value2) {
            addCriterion("project_id between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotBetween(String value1, String value2) {
            addCriterion("project_id not between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andTesterIsNull() {
            addCriterion("tester is null");
            return (Criteria) this;
        }

        public Criteria andTesterIsNotNull() {
            addCriterion("tester is not null");
            return (Criteria) this;
        }

        public Criteria andTesterEqualTo(String value) {
            addCriterion("tester =", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterNotEqualTo(String value) {
            addCriterion("tester <>", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterGreaterThan(String value) {
            addCriterion("tester >", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterGreaterThanOrEqualTo(String value) {
            addCriterion("tester >=", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterLessThan(String value) {
            addCriterion("tester <", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterLessThanOrEqualTo(String value) {
            addCriterion("tester <=", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterLike(String value) {
            addCriterion("tester like", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterNotLike(String value) {
            addCriterion("tester not like", value, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterIn(List<String> values) {
            addCriterion("tester in", values, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterNotIn(List<String> values) {
            addCriterion("tester not in", values, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterBetween(String value1, String value2) {
            addCriterion("tester between", value1, value2, "tester");
            return (Criteria) this;
        }

        public Criteria andTesterNotBetween(String value1, String value2) {
            addCriterion("tester not between", value1, value2, "tester");
            return (Criteria) this;
        }

        public Criteria andCreatorIsNull() {
            addCriterion("creator is null");
            return (Criteria) this;
        }

        public Criteria andCreatorIsNotNull() {
            addCriterion("creator is not null");
            return (Criteria) this;
        }

        public Criteria andCreatorEqualTo(String value) {
            addCriterion("creator =", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotEqualTo(String value) {
            addCriterion("creator <>", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThan(String value) {
            addCriterion("creator >", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThanOrEqualTo(String value) {
            addCriterion("creator >=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThan(String value) {
            addCriterion("creator <", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThanOrEqualTo(String value) {
            addCriterion("creator <=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLike(String value) {
            addCriterion("creator like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotLike(String value) {
            addCriterion("creator not like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorIn(List<String> values) {
            addCriterion("creator in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotIn(List<String> values) {
            addCriterion("creator not in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorBetween(String value1, String value2) {
            addCriterion("creator between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotBetween(String value1, String value2) {
            addCriterion("creator not between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalIsNull() {
            addCriterion("test_case_total is null");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalIsNotNull() {
            addCriterion("test_case_total is not null");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalEqualTo(Integer value) {
            addCriterion("test_case_total =", value, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalNotEqualTo(Integer value) {
            addCriterion("test_case_total <>", value, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalGreaterThan(Integer value) {
            addCriterion("test_case_total >", value, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("test_case_total >=", value, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalLessThan(Integer value) {
            addCriterion("test_case_total <", value, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalLessThanOrEqualTo(Integer value) {
            addCriterion("test_case_total <=", value, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalIn(List<Integer> values) {
            addCriterion("test_case_total in", values, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalNotIn(List<Integer> values) {
            addCriterion("test_case_total not in", values, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalBetween(Integer value1, Integer value2) {
            addCriterion("test_case_total between", value1, value2, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andTestCaseTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("test_case_total not between", value1, value2, "testCaseTotal");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseIsNull() {
            addCriterion("execute_test_case is null");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseIsNotNull() {
            addCriterion("execute_test_case is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseEqualTo(Integer value) {
            addCriterion("execute_test_case =", value, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseNotEqualTo(Integer value) {
            addCriterion("execute_test_case <>", value, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseGreaterThan(Integer value) {
            addCriterion("execute_test_case >", value, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseGreaterThanOrEqualTo(Integer value) {
            addCriterion("execute_test_case >=", value, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseLessThan(Integer value) {
            addCriterion("execute_test_case <", value, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseLessThanOrEqualTo(Integer value) {
            addCriterion("execute_test_case <=", value, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseIn(List<Integer> values) {
            addCriterion("execute_test_case in", values, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseNotIn(List<Integer> values) {
            addCriterion("execute_test_case not in", values, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseBetween(Integer value1, Integer value2) {
            addCriterion("execute_test_case between", value1, value2, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andExecuteTestCaseNotBetween(Integer value1, Integer value2) {
            addCriterion("execute_test_case not between", value1, value2, "executeTestCase");
            return (Criteria) this;
        }

        public Criteria andTestCasePassIsNull() {
            addCriterion("test_case_pass is null");
            return (Criteria) this;
        }

        public Criteria andTestCasePassIsNotNull() {
            addCriterion("test_case_pass is not null");
            return (Criteria) this;
        }

        public Criteria andTestCasePassEqualTo(Integer value) {
            addCriterion("test_case_pass =", value, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassNotEqualTo(Integer value) {
            addCriterion("test_case_pass <>", value, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassGreaterThan(Integer value) {
            addCriterion("test_case_pass >", value, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassGreaterThanOrEqualTo(Integer value) {
            addCriterion("test_case_pass >=", value, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassLessThan(Integer value) {
            addCriterion("test_case_pass <", value, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassLessThanOrEqualTo(Integer value) {
            addCriterion("test_case_pass <=", value, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassIn(List<Integer> values) {
            addCriterion("test_case_pass in", values, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassNotIn(List<Integer> values) {
            addCriterion("test_case_pass not in", values, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassBetween(Integer value1, Integer value2) {
            addCriterion("test_case_pass between", value1, value2, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCasePassNotBetween(Integer value1, Integer value2) {
            addCriterion("test_case_pass not between", value1, value2, "testCasePass");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailIsNull() {
            addCriterion("test_case_fail is null");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailIsNotNull() {
            addCriterion("test_case_fail is not null");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailEqualTo(Integer value) {
            addCriterion("test_case_fail =", value, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailNotEqualTo(Integer value) {
            addCriterion("test_case_fail <>", value, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailGreaterThan(Integer value) {
            addCriterion("test_case_fail >", value, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailGreaterThanOrEqualTo(Integer value) {
            addCriterion("test_case_fail >=", value, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailLessThan(Integer value) {
            addCriterion("test_case_fail <", value, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailLessThanOrEqualTo(Integer value) {
            addCriterion("test_case_fail <=", value, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailIn(List<Integer> values) {
            addCriterion("test_case_fail in", values, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailNotIn(List<Integer> values) {
            addCriterion("test_case_fail not in", values, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailBetween(Integer value1, Integer value2) {
            addCriterion("test_case_fail between", value1, value2, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseFailNotBetween(Integer value1, Integer value2) {
            addCriterion("test_case_fail not between", value1, value2, "testCaseFail");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockIsNull() {
            addCriterion("test_case_block is null");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockIsNotNull() {
            addCriterion("test_case_block is not null");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockEqualTo(Integer value) {
            addCriterion("test_case_block =", value, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockNotEqualTo(Integer value) {
            addCriterion("test_case_block <>", value, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockGreaterThan(Integer value) {
            addCriterion("test_case_block >", value, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockGreaterThanOrEqualTo(Integer value) {
            addCriterion("test_case_block >=", value, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockLessThan(Integer value) {
            addCriterion("test_case_block <", value, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockLessThanOrEqualTo(Integer value) {
            addCriterion("test_case_block <=", value, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockIn(List<Integer> values) {
            addCriterion("test_case_block in", values, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockNotIn(List<Integer> values) {
            addCriterion("test_case_block not in", values, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockBetween(Integer value1, Integer value2) {
            addCriterion("test_case_block between", value1, value2, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andTestCaseBlockNotBetween(Integer value1, Integer value2) {
            addCriterion("test_case_block not between", value1, value2, "testCaseBlock");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Long value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Long value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Long value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Long value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Long value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Long> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Long> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Long value1, Long value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Long value1, Long value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Long value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Long value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Long value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Long value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Long value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Long> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Long> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Long value1, Long value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Long value1, Long value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}