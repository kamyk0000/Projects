#ifndef PROJECT2_FILEMANAGER_H
#define PROJECT2_FILEMANAGER_H


#include <string>
#include "Record.h"
/**
 * class FileManager        odpowiada za zarządzanie menu oraz plików zawierających hasła
 * std::fstream fout, fin   strumienie wyjścia i wejścia odpowiadające za przesył danych między plikami tekstowymi i programem
 * std::string filePath     przechowuje informacje o scieżce obsługiwanego pliku
 * char key                 przechowuje informacje na temat klucza szyfrowania obsługiwanego pliku
 * RecordList rl            obiekt klasy RecordList jest źródłem informacji wyświetlanych przez metody klasy FileManager
 */
class FileManager {
    std::fstream fout, fin;
    std::string filePath, line;
    int option;
    char key;
    RecordList rl;

public:
    FileManager() {
    }
    /**
     * menu ()               odpowiada za obsługę głównego menu aplikacji, odbiera ona informację od użytkownika i umożliwia dalszą komunikację z innymi metodami
     * reateFile()           przyjmuje od użytkownika informacje (ścieżkę, nazwę, hasło) i na ich podstawie tworzy plik tekstowy
     * displayFiles()        wyświetla listę wszystkich (prócz CMakeList oraz temp) znajdujących się w bierzącym folderze plików tekstowych
     * openFile(), closeFile() za pomocą strumieni wejścia i wyjścia odpowiednio przekazują zawartość plików do szyfratora 'char cipher()'
     *                      i odszyfrowaną zawartość przekazują do dalszej pracy, a zaszyfrowaną zamykają w pliku
     */
    void menu();
    void createFile();
    void displayFiles();
    void openFile();
    void closeFile();
    /**
     * listMenu()           odpowiada i umożliwa użytkownikowi obsługę lub zmianę (za pomocą metod klasy RecordList oraz Record) listy haseł
     * passMenu()           obsługuje menu tworzenia haseł
     * listheader()         drukuje nagłówek listy
     * sortMenu()
     */
    void listMenu();
    void passMenu();
    void sortMenu();
    static void listHeader();
    /**
     * editMenu()           obsługuje menu edycji danego hasła
     * @param id jest indeksem danego hasła poddanego edycji przez użytkownika
     */
    void editMenu(int id);
    /**
     * szyfruje i deszyfruje znaki
     * @param c jest znakiem który będzie modyfikowany za pomocą klucza 'key' przy pomocy zestawienia XOR
     * @return zwraca za/deszyfrowany znak
     */
    char cipher (char c);
    /**
     * tworzy timestamp modyfikacji pliku ukryty w linijce znaków
     * przechowuje w sobe również flagę umożliwającą weryfikację czy podane hasło jest prawidłowe dla danego pliku
     * @param var potrzebny jest do ustalenie czy klucz ma się nie zmienić (w przypadku złego hasła)
     * @return
     */
    std::string getTimestamp(std::string var);
    /**
     * poniższe metody działają na fladze determinującej zgodność hasła
     */
    std::string newFlag();
    std::string oldFlag(std::string flag);
    bool checkFlag(std::string cflag);
    void checkInput();

};


#endif //PROJECT2_FILEMANAGER_H
