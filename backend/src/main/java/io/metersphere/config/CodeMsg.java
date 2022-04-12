package io.metersphere.config;

public class CodeMsg {
    private int code;
    private String message;

    public CodeMsg(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static CodeMsg SUCCESS = new CodeMsg(10000000, "成功");
    public static CodeMsg SERVER_EXCEPTION = new CodeMsg(10000001, "服务端异常");
    public static CodeMsg PARAMETER_ISNULL = new CodeMsg(10000002, "输入参数有误");
    public static CodeMsg PROJECT_NOT_FOUND = new CodeMsg(10000003, "项目不存在");
    public static CodeMsg PROJECT_CODING_EXIST = new CodeMsg(10000004, "项目与coding项目无对应关系");
    public static CodeMsg ITERATION_NOT_FOUND = new CodeMsg(10000005, "迭代不存在");


}
