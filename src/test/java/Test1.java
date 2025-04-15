import com.alibaba.fastjson.JSONObject;
import com.zishanshu.domain.User;
import lombok.AllArgsConstructor;
import lombok.ToString;

class B{
    Integer id;
}

@ToString
@AllArgsConstructor
class A{
    Integer id;
    Class<?> clazz;
}


public class Test1 {
    public static void main(String[] args) {
        A a = new A(1, B.class);

        byte[] buffer = JSONObject.toJSONBytes(a);
        A a2 = JSONObject.parseObject(buffer, A.class);

        System.out.println(a2);
    }
}
