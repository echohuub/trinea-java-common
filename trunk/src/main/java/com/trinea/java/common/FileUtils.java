package com.trinea.java.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 * 
 * @author Trinea 2012-5-12 下午03:42:05
 */
public class FileUtils {

    public final static String FILE_EXTENSION_SEPARATOR = ".";

    /**
     * 读文件
     * 
     * @param fileName
     * @return
     */
    public static StringBuilder readFile(String fileName) {
        File file = new File(fileName);
        StringBuilder fileContent = new StringBuilder();
        if (file != null && file.isFile()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line);
                }
                reader.close();
                return fileContent;
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                        return fileContent;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return fileContent;
                    } finally {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 写文件
     * 
     * @param fileName
     * @param content
     * @return
     */
    public static boolean writeFile(String fileName, StringBuilder content, boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName, append);
            fileWriter.write(content.toString());
            fileWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                }
            }
        }
    }

    /**
     * 读文件
     * 
     * @param fileName
     * @param endLabel 结束符号
     * @return
     */
    public static List<String> readFileToList(String fileName, List<String> endLabel) {
        File file = new File(fileName);
        List<String> fileContent = new ArrayList<String>();
        if (file != null && file.isFile()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = null;
                if (ListUtils.isEmpty(endLabel)) {
                    while ((line = reader.readLine()) != null) {
                        fileContent.add(line);
                    }
                } else {
                    while ((line = reader.readLine()) != null && !endLabel.contains(line)) {
                        fileContent.add(line);
                    }
                }
                reader.close();
                return fileContent;
            } catch (IOException e) {
                e.printStackTrace();
                return fileContent;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                        return fileContent;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return fileContent;
                    } finally {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 从路径中获得文件名（不包含后缀名）
     * 
     * @param filePath 文件名
     * @return 文件名（不包含后缀名）
     * @see
     * <pre>
     *      getFileName(null)               =   ""
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a"
     *      getFileName("a.b.rmvb")         =   "a.b"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\")              =   ""
     *      getFileName("c:\a")             =   "a"
     *      getFileName("c:\a.b")           =   "a"
     *      getFileName("c:a.txt\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b"
     * </pre>
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (!StringUtils.isEmpty(filePath)) {
            int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            int filePosi = filePath.lastIndexOf(File.separator);
            if (filePosi == -1) {
                return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
            } else {
                if (extenPosi == -1) {
                    return filePath.substring(filePosi + 1);
                } else {
                    return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
                }
            }
        }
        return "";
    }

    /**
     * 从路径中获得文件名（包含后缀名）
     * 
     * @param filePath 文件名
     * @return 文件名（包含后缀名）
     * @see
     * <pre>
     *      getFileName(null)               =   ""
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   ""
     *      getFileName("a.mp3")            =   ""
     *      getFileName("a.b.rmvb")         =   ""
     *      getFileName("abc")              =   ""
     *      getFileName("c:\")              =   "c:"
     *      getFileName("c:\a")             =   "c:"
     *      getFileName("c:\a.b")           =   "c:"
     *      getFileName("c:a.txt\a")        =   "c:a.txt"
     *      getFileName("c:a\b\c\d.txt")    =   "c:a\b\c"
     *      getFileName("/home/admin")      =   "/home"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     */
    public static String getDirName(String filePath) {
        return ((StringUtils.isEmpty(filePath) || filePath.lastIndexOf(File.separator) == -1) ? "" : filePath.substring(0,
                                                                                                                        filePath.lastIndexOf(File.separator)));
    }

    /**
     * 从路径中获得文件夹路径
     * 
     * @param filePath 文件名
     * @return 文件夹路径
     * @see
     * <pre>
     *      getFileName(null)               =   ""
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\")              =   ""
     *      getFileName("c:\a")             =   "a"
     *      getFileName("c:\a.b")           =   "a.b"
     *      getFileName("c:a.txt\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     */
    public static String getFileName(String filePath) {
        return (StringUtils.isEmpty(filePath) ? "" : filePath.substring(filePath.lastIndexOf(File.separator) + 1));
    }

    /**
     * 从路径中获得文件后缀
     * 
     * @param filePath 文件名
     * @return 后缀名
     * @see
     * <pre>
     *      getFileName(null)               =   ""
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   ""
     *      getFileName("a.mp3")            =   "mp3"
     *      getFileName("a.b.rmvb")         =   "rmvb"
     *      getFileName("abc")              =   ""
     *      getFileName("c:\")              =   ""
     *      getFileName("c:\a")             =   ""
     *      getFileName("c:\a.b")           =   "b"
     *      getFileName("c:a.txt\a")        =   "a"
     *      getFileName("/home/admin")      =   ""
     *      getFileName("/home/admin/a.txt/b")  =   ""
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     */
    public static String getFileExtension(String filePath) {
        if (!StringUtils.isBlank(filePath)) {
            int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            int filePosi = filePath.lastIndexOf(File.separator);
            if (filePosi == -1) {
                return (extenPosi == -1 ? "" : filePath.substring(extenPosi + 1));
            } else {
                if (extenPosi == -1) {
                    return "";
                } else {
                    return (filePosi < extenPosi ? filePath.substring(extenPosi + 1) : "");
                }
            }
        }
        return "";
    }

    /**
     * 根据文件路径循环创建文件的文件夹
     * 
     * @param filePath 文件路径
     * @return 是否成功创建文件夹，若文件夹已存在，返回true
     *         <ul>
     *         <li>若{@link FileUtils#getDirName}返回为空，返回false;</li>
     *         <li>若文件夹存在，返回true</li>
     *         <li>否则返回{@link java.io.File#makeFolder}</li>
     *         </ul>
     */
    public static boolean makeFolder(String filePath) {
        String folderName = getDirName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (!folder.exists()) ? folder.mkdirs() : true;
    }

    /**
     * 判断文件是否存在
     * 
     * @param filePath 文件路径
     * @return 存在返回true，否则返回false
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) return false;

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 判断文件夹是否存在
     * 
     * @param directoryPath 文件夹路径
     * @return 存在返回true，否则返回false
     */
    public static boolean isDirExist(String directoryPath) {
        if (StringUtils.isBlank(directoryPath)) return false;

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }
}
