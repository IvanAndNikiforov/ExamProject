import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Test implements Serializable {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<MyFile> listFiles = deserializationList();
        int choice = 1;
        String authorName;
        Scanner in = new Scanner(System.in);
        File pathDirectory = new File("TextFiles");
        if (!pathDirectory.exists()){
            pathDirectory.mkdir();
        }
        while (choice != 0) {
            System.out.println("Выберите действие: ");
            System.out.println("1 - Показать список файлов;");
            System.out.println("2 - Создать файл;");
            System.out.println("3 - Удалить файл;");
            System.out.println("4 - Выбрать файл;");
            System.out.println("5 - Сортировать список файлов;");
            System.out.println("0 - Выход;");
            choice = in.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.format("%15s%32s%10s%16s%31s", "Порядковый номер  |", "Имя файла  |",
                            "Размер  |", "Автор  |", "Дата создания  |");
                    System.out.print("\n_______________________________________________________________________________" +
                            "_____________________________\n");
                    for (int i = 0; i < listFiles.size(); i++) {
                        System.out.format("%19s%32s%10s%16s%20s", i + "  |", listFiles.get(i).getFileName() + "  |",
                                listFiles.get(i).getSize() + "  |", listFiles.get(i).getAuthor() + "  |",
                                listFiles.get(i).getDateCreation() + "  |");
                        System.out.print("\n__________________________________________________________________________" +
                                "__________________________________\n");
                    }
                }
                case 2 -> {
                    System.out.println("Введите имя автора: ");
                    authorName = in.next();
                    System.out.println("Введите имя файла: ");
                    String fileName = in.next();

                    listFiles.add(new MyFile(fileName, authorName));
                    serializationList(listFiles);
                }
                case 3 -> {
                    System.gc();
                    System.out.println("Введите порядковый номер файла, который нужно удалить: ");
                    int tmp = in.nextInt();
                    try {
                        Files.delete(Path.of(listFiles.get(tmp).getRootFolder() +
                                "\\" + listFiles.get(tmp).getFileName() + ".txt"));
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                    listFiles.remove(tmp);
                }
                case 4 -> {
                    System.out.println("Введите порядковый номер файла: ");
                    int numberFile = in.nextInt();
                    int choiceActions = 1;
                    String filePath = listFiles.get(numberFile).getRootFolder() +
                            "\\" + (String) listFiles.get(numberFile).getFileName() + ".txt";
                    while (choiceActions != 0) {
                        System.out.println("Выберите действие: ");
                        System.out.println("1 - Вывести текст на экран;");
                        System.out.println("2 - Поиск;");
                        System.out.println("3 - Замена;");
                        System.out.println("4 - Добавить;");
                        System.out.println("5 - Перезаписать;");
                        System.out.println("0 - Вернуться в предыдущее меню;");
                        choiceActions = in.nextInt();
                        switch (choiceActions) {
                            case 1:
                                listFiles.get(numberFile).readFile(listFiles.get(numberFile).getRootFolder() +
                                        "\\" + listFiles.get(numberFile).getFileName() + ".txt");
                                break;
                            case 2:
                                System.out.println("Введите слово для поиска: ");
                                String word = in.next();
                                List<Integer> foundIndex = MyFile.performKMPSearch(
                                        listFiles.get(numberFile).getRootFolder() +
                                                "\\" + listFiles.get(numberFile).getFileName() + ".txt", word);
                                if (foundIndex.isEmpty()) {
                                    System.out.println("Совпадений не найдено");
                                } else {
                                    MyFile.readFileSearch(filePath, foundIndex, word);
                                }
                                break;
                            case 3:
                                System.out.println("Введите слово которое нужно заменить: ");
                                String oldWord = in.next();
                                System.out.println("Введите слово для замены: ");
                                String newWord = in.next();
                                List<Integer> foundIndex1 = MyFile.performKMPSearch(
                                        listFiles.get(numberFile).getRootFolder() +
                                                "\\" + listFiles.get(numberFile).getFileName() + ".txt", oldWord);
                                if (foundIndex1.isEmpty()) {
                                    System.out.println("Совпадений не найдено");
                                } else {

                                    for (Integer integer : foundIndex1) {
                                        FileReader textFileReader = new FileReader(listFiles.get(numberFile).getRootFolder() +
                                                "\\" + listFiles.get(numberFile).getFileName() + ".txt");
                                        int chars;
                                        String str = "";
                                        while ((chars = textFileReader.read()) != -1) {
                                            str += (char) chars;
                                        }
                                        str = str.replace(oldWord, newWord);
                                        FileWriter textFileWriter = new FileWriter(listFiles.get(numberFile).getRootFolder() +
                                                "\\" + listFiles.get(numberFile).getFileName() + ".txt");
                                        textFileWriter.write(str);
                                        textFileReader.close();
                                        textFileWriter.close();
                                    }
                                }
                                break;
                            case 4:
                                System.out.println("Введите текст который необходимо добавить: ");
                                in.nextLine();
                                String text = in.nextLine();
                                try {
                                    FileWriter writer = new FileWriter(filePath, true);
                                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                                    bufferedWriter.write(text);
                                    bufferedWriter.close();
                                } catch (IOException e) {
                                    System.out.println(e);
                                }
                                break;
                            case 5:
                                System.out.println("Введите текст: ");
                                text = in.next();
                                try {
                                    FileWriter writer = new FileWriter(filePath);
                                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                                    bufferedWriter.write(text);
                                    bufferedWriter.close();
                                } catch (IOException e) {
                                    System.out.println(e);
                                }
                                break;
                            case 0:
                                break;
                        }
                    }
                }
                case 5 -> {
                    int choiceSort = 1;
                    while (choiceSort != 0) {
                        System.out.println("1 - Сортировка по имени;");
                        System.out.println("2 - Сортировка по дате;");
                        System.out.println("3 - Сортировка по размеру;");
                        System.out.println("0 - Вернуться в предыдущее меню;");
                        choiceSort = in.nextInt();
                        switch (choiceSort) {
                            case 1:
                                listFiles.sort(NameComparator);
                                break;
                            case 2:
                                listFiles.sort(DateComparator);
                                break;
                            case 3:
                                listFiles.sort(SizeComparator);
                                break;
                            case 0:
                                break;
                        }
                    }
                }
                case 0 -> serializationList(listFiles);
            }
        }
    }

    private static List<MyFile> deserializationList() throws IOException, ClassNotFoundException {
        List<MyFile> newList = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    "save.ser"));
            newList = ((List<MyFile>) ois.readObject());
            ois.close();
        } catch (EOFException | WriteAbortedException ex) {
            System.out.println("Список файлов пуст.");
        } catch (FileNotFoundException e) {
            FileOutputStream outputStream = new FileOutputStream("save.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        }
        return newList;
    }

    private static void serializationList(List<MyFile> listFiles) throws IOException {
        FileOutputStream outputStream = new FileOutputStream("save.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(listFiles);
        objectOutputStream.close();
        outputStream.close();
    }

    public static Comparator<MyFile> NameComparator = Comparator.comparing(MyFile::getFileName);

    public static Comparator<MyFile> DateComparator = Comparator.comparing(MyFile::getDateCreation);

    public static Comparator<MyFile> SizeComparator = Comparator.comparingInt(MyFile::getSize);
}
