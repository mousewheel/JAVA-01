import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

/***
 * Hello 类加载器
 */
public class HelloClassLoader extends ClassLoader {

    /**
     * 运行测试结果
     * @param args
     */
    public static void main(String[] args) {
        try {
            Class<?> clazz = new HelloClassLoader().findClass("Hello");
            Method hello = clazz.getDeclaredMethod("hello");
            hello.invoke(clazz.newInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            InputStream fin = getSystemClassLoader().getResourceAsStream("Week_01/Hello.xlass");
            byte[] bs = new byte[8];
            int count;
            while ((count =fin.read(bs) )> 0) {
                os.write(bs,0,count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        byte[] classBytes = decode(os.toByteArray());
        return defineClass(name, classBytes, 0, classBytes.length);
    }

    /**
     * decode
     * 所有字节通过x=255-x处理
     * @param xlassBytes
     * @return
     */
    private byte[] decode(byte[] xlassBytes) {
        for (int i = 0; i < xlassBytes.length; i++) {
            xlassBytes[i] = (byte) (255 - xlassBytes[i]);
        }
        return xlassBytes;
    }
}
