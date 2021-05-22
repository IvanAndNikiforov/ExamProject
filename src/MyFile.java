import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MyFile implements Serializable {

    private static final long serialVersionUID = 6529685098267757690L;

    private int size;
    private final String author;
    private final Date dateCreation;
    private static final String rootFolder = "TextFiles";
    private static final int sizePage = 1024;
    private String fileName;

    public MyFile(String fileName, String author) throws IOException {
        this.author = author;
        this.fileName = fileName;
        Calendar calendar = new GregorianCalendar();
        dateCreation = calendar.getTime();
        createFile(fileName);
    }

    public String getFileName() {
        return fileName;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public int getSize() {
        return size;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public String getAuthor() {
        return author;
    }

    public void createFile(String fileName) throws IOException {
        File dir = new File(rootFolder);
        List<String> lst = new ArrayList<>();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile()) {
                lst.add(file.getName());
            }
        }
        int counterNewFile = 0;
        for (String s : lst) {
            int a = 0;
            String tmp = s;
            int end = tmp.length() - 4;
            tmp = tmp.substring(0, end);
            String digitalPart = tmp;
            if (tmp.length() >= 7) {
                tmp = tmp.substring(0, 7);
                if (tmp.equals("NewFile")) {
                    digitalPart = digitalPart.substring(7);
                    if (!digitalPart.equals("")) {
                        a = Integer.parseInt(digitalPart);
                    }
                    if (a > counterNewFile) {
                        counterNewFile = a;
                    }
                }
            }
            if (tmp.equals(fileName)) {
                System.out.println("Файл с таким именем уже существует.");
                System.out.println("Имя созданного файла будет: NewFile" + (counterNewFile + 1));
                fileName = "NewFile" + (counterNewFile + 1);
            }
        }
        Files.createFile(Path.of(rootFolder + "\\" + fileName + ".txt"));
        FileWriter writer = new FileWriter(rootFolder + "\\" + fileName + ".txt");
        System.out.println("Введите текст: ");
        Scanner in = new Scanner(System.in);
        String text = in.nextLine();
        writer.write(text);
        writer.flush();
        this.fileName = fileName;
        File file1 = new File(rootFolder + "\\" + fileName + ".txt");
        size = (int) file1.length();
    }

    public void readFile(String fileName) throws IOException {
        Scanner in = new Scanner(System.in);
        int choice = 1;
        List<String> arrayPage = new ArrayList<>();
        int counter = 0;
        FileReader textFileReader = new FileReader(fileName);
        char[] buffer = new char[sizePage];
        int chars;
        while ((chars = textFileReader.read(buffer)) != -1) {
            textFileReader.skip(counter);
            arrayPage.add(String.valueOf(buffer, 0, chars));
        }
        textFileReader.close();
        int choiceNumberPage = 0;
        System.out.println(arrayPage.get(0));
        while (choice != 0) {
            System.out.println("1 - Назад.");
            System.out.println("2 - Ввести номер страницы.");
            System.out.println("3 - Вперед.");
            System.out.println("0 - Выход.");
            choice = in.nextInt();
            switch (choice) {
                case 1:
                    if (choiceNumberPage == 0) {
                        System.out.println(arrayPage.get(0));
                    } else {
                        choiceNumberPage--;
                        System.out.println(arrayPage.get(choiceNumberPage));
                    }
                    break;
                case 2:
                    System.out.println("Введите номер страницы");
                    choiceNumberPage = in.nextInt();
                    System.out.println(arrayPage.get(choiceNumberPage));
                    break;
                case 3:
                    if (choiceNumberPage == (arrayPage.size() - 1)) {
                        System.out.println(arrayPage.get(choiceNumberPage));
                    } else {
                        choiceNumberPage++;
                        System.out.println(arrayPage.get(choiceNumberPage));
                    }
                    break;
            }
        }
    }

   /* private int determinePageSize(String str, String fileName, int numberPage) throws IOException { //TODO: Возник вопрос, как сделать лучше, как реализовано сейчас, создать список в который поместили "страницы" или при чтении каждой страницы открывать и закрывать файл, нужен Ваш совет.
        byte[] utf8 = null;
        utf8 = printPage(fileName, 1).getBytes("UTF-8");
        int pageSize = utf8.length;
        return pageSize;
    }*/

    public static void readFileSearch(String fileName, List<Integer> foundIndex, String word) throws IOException {
        Scanner in = new Scanner(System.in);
        int choice = 1;
        File file = new File(fileName);
        int counter = 0;
        int numberPage = (int) (Math.ceil((double) foundIndex.get(counter) / (double) sizePage));
        if (numberPage == 0) {
            numberPage++;
        }
        String str = printPage(fileName, numberPage);
        str = str.replace(word, ("\u001B[31m" + word + "\u001B[0m"));
        System.out.println(str);
        while (choice != 0) {
            System.out.println("1 - Назад");
            System.out.println("2 - Далее");
            System.out.println("0 - Вернуться в предыдущее меню");
            choice = in.nextInt();
            switch (choice) {
                case 1:
                    counter--;
                    if (counter < 0) {
                        numberPage = (int) (Math.ceil((double) foundIndex.get(0) / (double) sizePage));
                        counter = 0;
                    } else {
                        numberPage = (int) (Math.ceil((double) foundIndex.get(counter) / (double) sizePage));
                    }
                    str = String.valueOf(printPage(fileName, numberPage + 1));
                    str = str.replace(word, ("\u001B[31m" + word + "\u001B[0m"));
                    System.out.println(str);
                    break;
                case 2:
                    counter++;
                    if (counter > (foundIndex.size() - 1)) {
                        numberPage = (int) (Math.ceil((double) foundIndex.get(foundIndex.size() - 1) / (double) sizePage));
                        counter--;
                    } else {
                        numberPage = (int) (Math.ceil((double) foundIndex.get(counter) / (double) sizePage));
                    }
                    str = printPage(fileName, numberPage + 1);
                    str = str.replace(word, ("\u001B[31m" + word + "\u001B[0m"));
                    System.out.println(str);
                    break;
            }
        }
    }

    public static String printPage(String fileName, int numberPage) throws IOException {
        FileReader textFileReader = new FileReader(fileName);
        char[] buffer = new char[sizePage];
        textFileReader.skip((numberPage - 1) * sizePage);
        int chars;
        String str = "";
        if ((chars = textFileReader.read(buffer)) != -1) {
            str = String.valueOf(buffer, 0, chars);
        }
        textFileReader.close();
        return str;
    }

    public static int[] compilePatternArray(String pattern) {
        int patternLength = pattern.length();
        int len = 0;
        int i = 1;
        int[] compliedPatternArray = new int[patternLength];
        compliedPatternArray[0] = 0;
        while (i < patternLength) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                compliedPatternArray[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = compliedPatternArray[len - 1];
                } else {
                    compliedPatternArray[i] = len;
                    i++;
                }
            }
        }
        return compliedPatternArray;
    }

    public static List<Integer> performKMPSearch(String fileName, String pattern) throws IOException {
        int[] compliedPatternArray = compilePatternArray(pattern);
        int textIndex = 0;
        int patternIndex = 0;
        FileReader textFileReader = new FileReader(fileName);
        char[] buffer = new char[1024];
        int chars;
        int correctionFactor = 0;
        List<Integer> foundIndex = new ArrayList<>();
        while ((chars = textFileReader.read(buffer)) != -1) {
            if (chars < 1024) {
                buffer = Arrays.copyOf(buffer, chars);
            }
            String text = String.valueOf(buffer, 0, chars);
            while (textIndex < text.length()) {
                if (pattern.charAt(patternIndex) == text.charAt(textIndex)) {
                    patternIndex++;
                    textIndex++;
                }
                if (patternIndex == pattern.length()) {
                    foundIndex.add((textIndex - patternIndex) + correctionFactor);
                    patternIndex = compliedPatternArray[patternIndex - 1];
                } else if (textIndex < text.length() && pattern.charAt(patternIndex) != text.charAt(textIndex)) {
                    if (patternIndex != 0) {
                        patternIndex = compliedPatternArray[patternIndex - 1];
                    } else {
                        textIndex = textIndex + 1;
                    }
                }
            }
            textIndex = 0;
            patternIndex = 0;
            correctionFactor += 1024;
        }
        return foundIndex;
    }
}
