#ifndef PROJECT2_RECORD_H
#define PROJECT2_RECORD_H


#include <string>
#include <vector>

/**
 * class Record odpowiada za tworzenie obiektów przechowujących dane związane z hasłami
 */
class Record {
public:
    std::string password, category, login, description;

    Record();
    Record(std::string pass);
    Record( std::string pass, std::string cat, std::string log, std::string desc);

    //friend class RecordList;

    /**
     * wyświetla na konsoli dane dotyczące rekordu o indexie 'i'
     * @param i index rekordu
     */
    void showRecord (int i);
};
/**
 * class RecordList odpowiada za utworzenie vectora zbioru obiektów klasy Record
 * oraz przetwarzaniu na nich operacji dzięki parametrom uzyskanym przez klasę FileManager od użytkownika
 */
class RecordList {
public:
    /**
     * główna lista przechowująca dane o obiektach Record
     */
    std::vector<Record> list;
    /**
     * lista przechowująca dane na temat aktualnych kategorii haseł
     */
    std::vector<std::string> categories;
    /**
     * znak półspacji używany do zapisu i odczytu z pliku części obiektów klasy Record
     * nie są niemożliwe do użycia przez użytkownika w czasie pracy w programie,
     * jednak bardziej nieprawdopodobne
     */
    const char s = 32;

    /**
     * sprawdza czy dany index nie jest poza granicami listy
     * @param i index
     * @return
     */
    bool checkIndex(int i);
    /**
     * loadRecords() zapisuje do listy odczytane informacje z pliku jako obiekty typu Record
     * analogicznie saveRecords() zapisuje informacji z listy do pliku danej ścieżki
     * @param path scieżka do pliku
     */
    void loadRecords(const std::string& path);
    void saveRecords(const std::string& path);
    /**
     * przeszukuję liste haseł dla pdanego przez użytkownika ciągu znaków przy pomocy regexa
     * a następnie ustawia je kolejno na szcycie listy i wyświetla na terminalu
     * @param str ciąg znaków do regexa
     */
    void searchList(const std::string& str);
    /**
     * sprawdza czy dane hasło (tworzone) znajduje się już w bazie, aby użytkownik mógł wybrać czy chce go użyć czy stworzyć inne
     * @param password sprawdzane hasło
     * @return
     */
    bool isDuplicate(const std::string& password);
    /**
     * tworzy nowy obiekt klasy Record na podstawie podanego argumentu hasła
     * @param password hasło
     */
    void addPassword(const std::string& password);
    /**
     * usuwa obiekt typu Record z listy za pomocą argumrntu indeksowania
     * @param id indeks obiektu
     */
    void deletePassword(int id);
    /**
     * generuje losowe hasło w zależności od podanych argumentów przez użytkownika, długości, czy ma zawierać duże lub/i specjalne znaki
     * @param length długość hasła
     * @param special wybieranie ze znaków specjalnych
     * @param big wybieranie ze znaków dużych
     * @return
     */
    std::string generatePassword(int length, bool special, bool big);

    bool isDupeCat(const std::string& cat);
    /**
     * dodaje kategorię o wartości argumentu cat
     * @param cat
     */
    void addCat(const std::string& cat);
    /**
     * usuwa ktegorię o wartości cat oraz wszystkie obiekty Record o tej samej wartości kategorii
     * @param cat
     */
    void removeCat(const std::string& cat);
    void updateCat(int i, const std::string& cat);
    /**
     * sortuje listę w oparciu o wybrany przez użytkownika argument
     * @param by pass / cat / passncat
     */
    void sortList(std::string by);

};


#endif //PROJECT2_RECORD_H
