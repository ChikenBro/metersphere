package io.metersphere.base.domain;

import java.util.ArrayList;
import java.util.List;

public class IssueTrendExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public IssueTrendExample() {
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

        public Criteria andJiraKeyIsNull() {
            addCriterion("jira_key is null");
            return (Criteria) this;
        }

        public Criteria andJiraKeyIsNotNull() {
            addCriterion("jira_key is not null");
            return (Criteria) this;
        }

        public Criteria andJiraKeyEqualTo(String value) {
            addCriterion("jira_key =", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyNotEqualTo(String value) {
            addCriterion("jira_key <>", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyGreaterThan(String value) {
            addCriterion("jira_key >", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyGreaterThanOrEqualTo(String value) {
            addCriterion("jira_key >=", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyLessThan(String value) {
            addCriterion("jira_key <", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyLessThanOrEqualTo(String value) {
            addCriterion("jira_key <=", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyLike(String value) {
            addCriterion("jira_key like", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyNotLike(String value) {
            addCriterion("jira_key not like", value, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyIn(List<String> values) {
            addCriterion("jira_key in", values, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyNotIn(List<String> values) {
            addCriterion("jira_key not in", values, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyBetween(String value1, String value2) {
            addCriterion("jira_key between", value1, value2, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andJiraKeyNotBetween(String value1, String value2) {
            addCriterion("jira_key not between", value1, value2, "jiraKey");
            return (Criteria) this;
        }

        public Criteria andIssueWeekIsNull() {
            addCriterion("issue_week is null");
            return (Criteria) this;
        }

        public Criteria andIssueWeekIsNotNull() {
            addCriterion("issue_week is not null");
            return (Criteria) this;
        }

        public Criteria andIssueWeekEqualTo(Integer value) {
            addCriterion("issue_week =", value, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekNotEqualTo(Integer value) {
            addCriterion("issue_week <>", value, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekGreaterThan(Integer value) {
            addCriterion("issue_week >", value, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekGreaterThanOrEqualTo(Integer value) {
            addCriterion("issue_week >=", value, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekLessThan(Integer value) {
            addCriterion("issue_week <", value, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekLessThanOrEqualTo(Integer value) {
            addCriterion("issue_week <=", value, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekIn(List<Integer> values) {
            addCriterion("issue_week in", values, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekNotIn(List<Integer> values) {
            addCriterion("issue_week not in", values, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekBetween(Integer value1, Integer value2) {
            addCriterion("issue_week between", value1, value2, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andIssueWeekNotBetween(Integer value1, Integer value2) {
            addCriterion("issue_week not between", value1, value2, "issueWeek");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeIsNull() {
            addCriterion("start_week_time is null");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeIsNotNull() {
            addCriterion("start_week_time is not null");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeEqualTo(String value) {
            addCriterion("start_week_time =", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeNotEqualTo(String value) {
            addCriterion("start_week_time <>", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeGreaterThan(String value) {
            addCriterion("start_week_time >", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeGreaterThanOrEqualTo(String value) {
            addCriterion("start_week_time >=", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeLessThan(String value) {
            addCriterion("start_week_time <", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeLessThanOrEqualTo(String value) {
            addCriterion("start_week_time <=", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeLike(String value) {
            addCriterion("start_week_time like", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeNotLike(String value) {
            addCriterion("start_week_time not like", value, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeIn(List<String> values) {
            addCriterion("start_week_time in", values, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeNotIn(List<String> values) {
            addCriterion("start_week_time not in", values, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeBetween(String value1, String value2) {
            addCriterion("start_week_time between", value1, value2, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andStartWeekTimeNotBetween(String value1, String value2) {
            addCriterion("start_week_time not between", value1, value2, "startWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeIsNull() {
            addCriterion("end_week_time is null");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeIsNotNull() {
            addCriterion("end_week_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeEqualTo(String value) {
            addCriterion("end_week_time =", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeNotEqualTo(String value) {
            addCriterion("end_week_time <>", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeGreaterThan(String value) {
            addCriterion("end_week_time >", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeGreaterThanOrEqualTo(String value) {
            addCriterion("end_week_time >=", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeLessThan(String value) {
            addCriterion("end_week_time <", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeLessThanOrEqualTo(String value) {
            addCriterion("end_week_time <=", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeLike(String value) {
            addCriterion("end_week_time like", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeNotLike(String value) {
            addCriterion("end_week_time not like", value, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeIn(List<String> values) {
            addCriterion("end_week_time in", values, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeNotIn(List<String> values) {
            addCriterion("end_week_time not in", values, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeBetween(String value1, String value2) {
            addCriterion("end_week_time between", value1, value2, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andEndWeekTimeNotBetween(String value1, String value2) {
            addCriterion("end_week_time not between", value1, value2, "endWeekTime");
            return (Criteria) this;
        }

        public Criteria andIssueTotalIsNull() {
            addCriterion("issue_total is null");
            return (Criteria) this;
        }

        public Criteria andIssueTotalIsNotNull() {
            addCriterion("issue_total is not null");
            return (Criteria) this;
        }

        public Criteria andIssueTotalEqualTo(Integer value) {
            addCriterion("issue_total =", value, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalNotEqualTo(Integer value) {
            addCriterion("issue_total <>", value, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalGreaterThan(Integer value) {
            addCriterion("issue_total >", value, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("issue_total >=", value, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalLessThan(Integer value) {
            addCriterion("issue_total <", value, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalLessThanOrEqualTo(Integer value) {
            addCriterion("issue_total <=", value, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalIn(List<Integer> values) {
            addCriterion("issue_total in", values, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalNotIn(List<Integer> values) {
            addCriterion("issue_total not in", values, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalBetween(Integer value1, Integer value2) {
            addCriterion("issue_total between", value1, value2, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andIssueTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("issue_total not between", value1, value2, "issueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalIsNull() {
            addCriterion("resolution_issue_total is null");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalIsNotNull() {
            addCriterion("resolution_issue_total is not null");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalEqualTo(Integer value) {
            addCriterion("resolution_issue_total =", value, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalNotEqualTo(Integer value) {
            addCriterion("resolution_issue_total <>", value, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalGreaterThan(Integer value) {
            addCriterion("resolution_issue_total >", value, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("resolution_issue_total >=", value, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalLessThan(Integer value) {
            addCriterion("resolution_issue_total <", value, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalLessThanOrEqualTo(Integer value) {
            addCriterion("resolution_issue_total <=", value, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalIn(List<Integer> values) {
            addCriterion("resolution_issue_total in", values, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalNotIn(List<Integer> values) {
            addCriterion("resolution_issue_total not in", values, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalBetween(Integer value1, Integer value2) {
            addCriterion("resolution_issue_total between", value1, value2, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionIssueTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("resolution_issue_total not between", value1, value2, "resolutionIssueTotal");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueIsNull() {
            addCriterion("resolution_week_issue is null");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueIsNotNull() {
            addCriterion("resolution_week_issue is not null");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueEqualTo(Integer value) {
            addCriterion("resolution_week_issue =", value, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueNotEqualTo(Integer value) {
            addCriterion("resolution_week_issue <>", value, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueGreaterThan(Integer value) {
            addCriterion("resolution_week_issue >", value, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueGreaterThanOrEqualTo(Integer value) {
            addCriterion("resolution_week_issue >=", value, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueLessThan(Integer value) {
            addCriterion("resolution_week_issue <", value, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueLessThanOrEqualTo(Integer value) {
            addCriterion("resolution_week_issue <=", value, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueIn(List<Integer> values) {
            addCriterion("resolution_week_issue in", values, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueNotIn(List<Integer> values) {
            addCriterion("resolution_week_issue not in", values, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueBetween(Integer value1, Integer value2) {
            addCriterion("resolution_week_issue between", value1, value2, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionWeekIssueNotBetween(Integer value1, Integer value2) {
            addCriterion("resolution_week_issue not between", value1, value2, "resolutionWeekIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueIsNull() {
            addCriterion("resolution_history_issue is null");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueIsNotNull() {
            addCriterion("resolution_history_issue is not null");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueEqualTo(Integer value) {
            addCriterion("resolution_history_issue =", value, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueNotEqualTo(Integer value) {
            addCriterion("resolution_history_issue <>", value, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueGreaterThan(Integer value) {
            addCriterion("resolution_history_issue >", value, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueGreaterThanOrEqualTo(Integer value) {
            addCriterion("resolution_history_issue >=", value, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueLessThan(Integer value) {
            addCriterion("resolution_history_issue <", value, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueLessThanOrEqualTo(Integer value) {
            addCriterion("resolution_history_issue <=", value, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueIn(List<Integer> values) {
            addCriterion("resolution_history_issue in", values, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueNotIn(List<Integer> values) {
            addCriterion("resolution_history_issue not in", values, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueBetween(Integer value1, Integer value2) {
            addCriterion("resolution_history_issue between", value1, value2, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andResolutionHistoryIssueNotBetween(Integer value1, Integer value2) {
            addCriterion("resolution_history_issue not between", value1, value2, "resolutionHistoryIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueIsNull() {
            addCriterion("test_env_issue is null");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueIsNotNull() {
            addCriterion("test_env_issue is not null");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueEqualTo(Integer value) {
            addCriterion("test_env_issue =", value, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueNotEqualTo(Integer value) {
            addCriterion("test_env_issue <>", value, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueGreaterThan(Integer value) {
            addCriterion("test_env_issue >", value, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueGreaterThanOrEqualTo(Integer value) {
            addCriterion("test_env_issue >=", value, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueLessThan(Integer value) {
            addCriterion("test_env_issue <", value, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueLessThanOrEqualTo(Integer value) {
            addCriterion("test_env_issue <=", value, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueIn(List<Integer> values) {
            addCriterion("test_env_issue in", values, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueNotIn(List<Integer> values) {
            addCriterion("test_env_issue not in", values, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueBetween(Integer value1, Integer value2) {
            addCriterion("test_env_issue between", value1, value2, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andTestEnvIssueNotBetween(Integer value1, Integer value2) {
            addCriterion("test_env_issue not between", value1, value2, "testEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueIsNull() {
            addCriterion("uat_env_issue is null");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueIsNotNull() {
            addCriterion("uat_env_issue is not null");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueEqualTo(Integer value) {
            addCriterion("uat_env_issue =", value, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueNotEqualTo(Integer value) {
            addCriterion("uat_env_issue <>", value, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueGreaterThan(Integer value) {
            addCriterion("uat_env_issue >", value, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueGreaterThanOrEqualTo(Integer value) {
            addCriterion("uat_env_issue >=", value, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueLessThan(Integer value) {
            addCriterion("uat_env_issue <", value, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueLessThanOrEqualTo(Integer value) {
            addCriterion("uat_env_issue <=", value, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueIn(List<Integer> values) {
            addCriterion("uat_env_issue in", values, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueNotIn(List<Integer> values) {
            addCriterion("uat_env_issue not in", values, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueBetween(Integer value1, Integer value2) {
            addCriterion("uat_env_issue between", value1, value2, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andUatEnvIssueNotBetween(Integer value1, Integer value2) {
            addCriterion("uat_env_issue not between", value1, value2, "uatEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueIsNull() {
            addCriterion("prod_env_issue is null");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueIsNotNull() {
            addCriterion("prod_env_issue is not null");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueEqualTo(Integer value) {
            addCriterion("prod_env_issue =", value, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueNotEqualTo(Integer value) {
            addCriterion("prod_env_issue <>", value, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueGreaterThan(Integer value) {
            addCriterion("prod_env_issue >", value, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueGreaterThanOrEqualTo(Integer value) {
            addCriterion("prod_env_issue >=", value, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueLessThan(Integer value) {
            addCriterion("prod_env_issue <", value, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueLessThanOrEqualTo(Integer value) {
            addCriterion("prod_env_issue <=", value, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueIn(List<Integer> values) {
            addCriterion("prod_env_issue in", values, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueNotIn(List<Integer> values) {
            addCriterion("prod_env_issue not in", values, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueBetween(Integer value1, Integer value2) {
            addCriterion("prod_env_issue between", value1, value2, "prodEnvIssue");
            return (Criteria) this;
        }

        public Criteria andProdEnvIssueNotBetween(Integer value1, Integer value2) {
            addCriterion("prod_env_issue not between", value1, value2, "prodEnvIssue");
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

        public Criteria andIssueYearIsNull() {
            addCriterion("issue_year is null");
            return (Criteria) this;
        }

        public Criteria andIssueYearIsNotNull() {
            addCriterion("issue_year is not null");
            return (Criteria) this;
        }

        public Criteria andIssueYearEqualTo(Integer value) {
            addCriterion("issue_year =", value, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearNotEqualTo(Integer value) {
            addCriterion("issue_year <>", value, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearGreaterThan(Integer value) {
            addCriterion("issue_year >", value, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearGreaterThanOrEqualTo(Integer value) {
            addCriterion("issue_year >=", value, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearLessThan(Integer value) {
            addCriterion("issue_year <", value, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearLessThanOrEqualTo(Integer value) {
            addCriterion("issue_year <=", value, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearIn(List<Integer> values) {
            addCriterion("issue_year in", values, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearNotIn(List<Integer> values) {
            addCriterion("issue_year not in", values, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearBetween(Integer value1, Integer value2) {
            addCriterion("issue_year between", value1, value2, "issueYear");
            return (Criteria) this;
        }

        public Criteria andIssueYearNotBetween(Integer value1, Integer value2) {
            addCriterion("issue_year not between", value1, value2, "issueYear");
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