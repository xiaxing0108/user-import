package constant;

/**
 * 公共静态变量类,后期如果数据过多将转换为数据库配置
 */
public class CommonConstant {

    //登陆验证码失效时间为60秒
    public static final Long LOGIN_CHECK_CODE_EXPIRE = 60L;

    //MD5数组
    public static final char[] HEX_DIGITS={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    //数字大写字母数组
    public static final String[] CODE_ARRAY = {
            "A","B","C","D","E","F","G","H","I","J","K",
            "L","M","N","O","P","Q","R","S","T","U","V",
            "W","X","Y","Z","1","2","3","4","5","6","7",
            "8","9","0"
    };


}
