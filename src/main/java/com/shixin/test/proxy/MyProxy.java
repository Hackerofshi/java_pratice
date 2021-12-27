package com.shixin.test.proxy;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyProxy {
    public <T> T create(final Class<T> bookClass) {
        return (T) Proxy.newProxyInstance(bookClass.getClassLoader(), new Class<?>[]{bookClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //不走代理方法，直接走原始方法
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                return (Book) () -> System.out.println("test");
            }
        });
    }


    interface Book {
        void read();
    }

    public static void main(String[] args) {
        Book instance = new MyProxy().create(Book.class);
        try {

            // 将代理类转化成字节码数组，然后输出到本地 ，通过jad反编译class文件 （或者直接通过idea 打开class文件）
            byte[] bytes = sun.misc.ProxyGenerator.generateProxyClass(instance.getClass().getSimpleName(), instance.getClass().getInterfaces());
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\UsersDesktopproxy$Proxy0.class");
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    /* renamed from: $Proxy0 */  编译后的文件
//public final class $Proxy0 extends Proxy implements Book {
//    private static Method m0;
//    private static Method m1;
//    private static Method m2;
//    private static Method m3;
//
//    static {
//        try {
//            m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[]{Class.forName("java.lang.Object")});
//            m3 = Class.forName("com.shixin.test.MyProxy$Book").getMethod("read", new Class[0]);
//            m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
//            m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
//        } catch (NoSuchMethodException e) {
//            throw new NoSuchMethodError(e.getMessage());
//        } catch (ClassNotFoundException e2) {
//            throw new NoClassDefFoundError(e2.getMessage());
//        }
//    }
//
//    public $Proxy0(InvocationHandler invocationHandler) {
//        super(invocationHandler);
//    }
//
//    public final boolean equals(Object obj) {
//        try {
//            return ((Boolean) this.h.invoke(this, m1, new Object[]{obj})).booleanValue();
//        } catch (Error | RuntimeException e) {
//            throw e;
//        } catch (Throwable e2) {
//            UndeclaredThrowableException undeclaredThrowableException = new UndeclaredThrowableException(e2);
//        }
//    }
//
//    public final int hashCode() {
//        try {
//            return ((Integer) this.h.invoke(this, m0, null)).intValue();
//        } catch (Error | RuntimeException e) {
//            throw e;
//        } catch (Throwable e2) {
//            UndeclaredThrowableException undeclaredThrowableException = new UndeclaredThrowableException(e2);
//        }
//    }
//
//    public final void read() {
//        try {
//            this.h.invoke(this, m3, null);
//        } catch (Error | RuntimeException e) {
//            throw e;
//        } catch (Throwable e2) {
//            UndeclaredThrowableException undeclaredThrowableException = new UndeclaredThrowableException(e2);
//        }
//    }
//
//    public final String toString() {
//        try {
//            return (String) this.h.invoke(this, m2, null);
//        } catch (Error | RuntimeException e) {
//            throw e;
//        } catch (Throwable e2) {
//            UndeclaredThrowableException undeclaredThrowableException = new UndeclaredThrowableException(e2);
//        }
//    }
//}
}
