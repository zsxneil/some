package com.my.compress.zip4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class PasswordStr {
    //密码可能会包含的字符集合
    static char[] charSource = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n',  'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N',  'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '1',  '2', '3', '4', '5', '6', '7', '8', '9', '0',
            '!', '@', '#', '$', '%', '^', '&', '*'
            };
    static FileOutputStream out;

    static {
        try {
            out = new FileOutputStream(new File("F://password.txt"), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static int sLength = charSource.length; //字符集长度
    public static void main(String[] args) throws IOException {
        long beginMillis = System.currentTimeMillis();
        System.out.println(beginMillis);//开始时间

        int maxLength = 5; //设置可能最长的密码长度
        CrackPass(maxLength);

        long endMillis = System.currentTimeMillis();
        System.out.println(endMillis);//结束时间

        System.out.println(endMillis - beginMillis);//总耗时，毫秒
        out.close();

    }

    //得到密码长度从 1到maxLength的所有不同长的密码集合
    public static void CrackPass(int maxLength) throws IOException {
        for (int i = 1; i <= maxLength; i++)
        {
            char[] list =new char[i];
            Crack(list, i);
        }

    }

    //得到长度为len所有的密码组合，在字符集charSource中
    //递归表达式：fn(n)=fn(n-1)*sLenght; 大致是这个意思吧
    private static void Crack(char[] list, int len) throws IOException {
        if (len == 0)
        {  //递归出口，list char[] 转换为字符串，并打印
            //System.out.println(ArrayToString(list));
            out.write(ArrayToString(list).getBytes());
            out.write('\n');

        }
        else
        {
            for (int i = 0; i < sLength; i++)
            {
                list[len - 1] = charSource[i];
                Crack(list, len - 1);
            }
        }
    }

    //list char[] 转换为字符串
    private static String ArrayToString(char[] list)
    {
        if (list == null||list.length == 0)
            return "";
        StringBuilder buider = new StringBuilder(list.length*2);
        for (int i = 0; i < list.length; i++)
        {
            buider.append(list[i]);
        }
        return buider.toString();

    }
}
