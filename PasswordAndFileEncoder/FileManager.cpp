#include <iostream>
#include <filesystem>
#include <regex>
#include <fstream>
#include "FileManager.h"
#include "Record.h"

void FileManager::menu() {
    while (true) {
        std::cout
                << "\n====== [MENU] ======\n1) Create file\n2) Choose file\n3) Search file\n4) Exit\n====================\nType the correct number to choose a option:\n";
        std::cin >> option;
        checkInput();
        switch (option) {
            case 1:
                std::cout << "\nCreating a file";
                createFile();
                break;
            case 2:
                std::cout << "\nShowing all current directory files";
                displayFiles();
                break;
            case 3:
                std::cout << "\nSearching for a file\nType in your file path:\n";
                std::cin >> filePath;
                openFile();
                break;
            case 4:
                std::cout << "\nGoodbye!";
                exit(0);
            default:
                std::cout << "\nPlease choose correct option!";
        }
    }
}
void FileManager::createFile () {
    std::string path, name, password;

    std::cout << "\nType in file directory path (type [0] to use default current)\n"; //not sure
    std::cin >> path;
    if (path == "0") {
        path = std::filesystem::current_path().string();
    }
    std::cout << "\nType in file name (please specify the type '.txt')\n";
    std::cin >> name; // check for dupe name?
    std::cout << "\nType in your password\n";
    std::cin >> password; // potrzebne?
    path += "\\" + name;

    std::cout << "\nCREATED A FILE:" << path;
    filePath = path;
    openFile();
}
void FileManager::displayFiles () {
    auto dir = std::filesystem::directory_iterator(".");
    auto dirPath = std::filesystem::current_path();
    auto dirIter = std::filesystem::directory_iterator(dirPath);

    std::vector <std::string> vec;

    std::regex txt(".*.txt");
    int id, i = 0;
    for (auto const& entry : dirIter) {
        if (entry.path().filename().string() == "CMakeCache.txt" || entry.path().filename().string() == "temp.txt") {
            //
        } else if(std::regex_match(entry.path().filename().string(),txt)){
            vec.push_back(entry.path().string());
            std::cout << '\n' << "[" << i << "] " << entry.path().filename().string();
            i++;
        }
    }
    if (vec.empty()) {
        std::cout << "\nSorry, no files found in default directory...\n";
        return;
    }
    std::cout << "\nType in id number of the file to open it:\n";
    std::cin >> id;
    if (id < 0 || id > vec.size()-1) {
        std::cout << "\nSorry, no file of this id found...\n";
        return;
    } else filePath = (vec[id]);
    openFile();
}
std::string FileManager::getTimestamp(std::string var) {

    time_t now = time(0);
    struct tm tstruct;
    char buf1[80], buf2[80];
    tstruct = *localtime(&now);
    strftime(buf1, sizeof(buf1), "%Y-%m-%d", &tstruct);
    strftime(buf2, sizeof(buf2), "%X", &tstruct);
    std::string timestamp = var + buf1 + rl.generatePassword(10, true, true) + buf2 +
                            rl.generatePassword(11, true, true);
    return timestamp;
}

char FileManager::cipher(char c) {
    c = c ^ key;
    return c;
}
std::string FileManager::newFlag() {
    std::string flag = "abba";
    std::string cFlag;
    for (char f:flag) {
        cFlag += cipher(f);
    }
    return cFlag;
}
std::string FileManager::oldFlag(std::string cFlag) {
    std::string eFlag;
    for (int i = 0; i < 4; ++i) {
        eFlag += cFlag[i];
    }
    return eFlag;
}
bool FileManager::checkFlag(std::string cFlag) {
    std::string bFlag;
    for (int i = 0; i < 4; ++i) {
        bFlag += cipher(cFlag[i]);
    }
    return (bFlag == "abba");
}
void FileManager::openFile () { //encrytp decrypt
    std::string password;
    char c;
    key = 0;
    std::vector<std::string> content;
    std::string flagCheck, old;

    std::cout << "\nOpening file";

    fin.open(filePath, std::ios::in);
    std::getline(fin, line);
    flagCheck = line;
    old = oldFlag(flagCheck);
    while (std::getline(fin, line)) {
        content.push_back(line);
    }
    fin.close();
    if (!flagCheck.empty()) {
            std::cout << "\nType in your password: \n";
            std::cin >> password;

            for (char ch: password) {
                key += ch;
            }
            if (checkFlag(flagCheck)) {
                fout.open(filePath, std::ios::out);
                fout << getTimestamp(newFlag()) << "\n";
                for (const std::string &l: content) {
                    fout << l << "\n";
                }
                fout.close();
            } else {
                fout.open(filePath, std::ios::out);
                fout << getTimestamp(old) << "\n";
                for (const std::string &l: content) {
                    fout << l << "\n";
                }
                fout.close();
                std::cout << "Wrong password, you shouldn't be here...\n";
                return;
        }
    } else {
        std::cout << "\nType in your password: \n";
        std::cin >> password;
        for (char ch: password) {
            key += ch;
        }
    }
    content.clear();

    /*
    char key[password.length()];
    for (int j = 0; j < password.length(); ++j) {
        key[j] = password [j];
     }
     */

    fin.open(filePath, std::ios::in);
    fout.open("temp.txt", std::ios::out);
    std::getline(fin, line);
    fout << getTimestamp(newFlag()) << "\n";

    while (fin>>std::noskipws>>c) {
        fout << cipher(c);
    }
    fin.close();
    fout.close();

    // skopiowano orginał do tmp

    fin.open("temp.txt", std::ios::in);
    fout.open(filePath, std::ios::out);
    std::getline(fin, line);
    fout << getTimestamp(newFlag())<< "\n";
    while (fin>>std::noskipws>>c) {
        fout << c;
    }
    fin.close();
    fout.close();

    std::cout << "\nFile successfully opened\n";
    //remove("tmp.txt"); //clear
    //clear("tmp.txt");

    listMenu();
    closeFile();
}
void FileManager::closeFile() {
    char c;
    std::cout << "\nClosing file\n";
    fin.open(filePath, std::ios::in);
    fout.open("temp.txt", std::ios::out);

    std::getline(fin, line);
    fout << getTimestamp(newFlag()) << "\n";
    while (fin>>std::noskipws>>c) {
        fout << cipher(c);
    }
    fin.close();
    fout.close(); // przepisana kopia zenkryptowana do tmp

    fin.open("temp.txt", std::ios::in);
    fout.open(filePath, std::ios::out);
    std::getline(fin, line);
    fout << getTimestamp(newFlag()) << "\n";;
    while (fin>>std::noskipws>>c) {
        fout << c;
    }
    fin.close();
    fout.close();

    std::cout << "\nFile successfully closed\n";
    remove("tmp.txt"); //clear

}
void FileManager::listHeader() {
    std::cout << std::left << std::setw(5) << "[ID]"
              << std::left << std::setw(25) << "[PASSWORD]"
              << std::left << std::setw(20) << "[CATEGORY]"
              << std::left << std::setw(20) << "[LOGIN]"
              << std::left << std::setw(5) << "[DESCRIPTION]" << "\n";
}

void FileManager::passMenu() {
    std::string str, yn;
    int length;
    bool special, big;
    while (true) {
        std::cout
                << "\n======= [MAIN MENU] =======\n1) Type your own password\n2) Generate password\n===========================\nType the correct number to choose a option: \n";
        std::cin >> option;
        checkInput();
        switch (option) {
            case 1:
                std::cout << "\nType in new password to add (max 15 chars): \n";
                std::cin >> str;
                if (str.length() >= 15) {
                    std::cout << "This password is too long...\n";
                    return;
                }
                if (rl.isDuplicate(str)) {
                    std::cout << "This password is already in use, do you want to use it anyways? [Y]/[N]\n";
                    std::cin >> yn;
                    if ((yn == "Y") or (yn == "y")) {
                        rl.addPassword(str);
                        return;
                    } else if ((yn == "N") or (yn == "n")) {
                        break;
                    } else {
                        std::cout << "Please choose correct option!\n";
                    }
                }
                rl.addPassword(str);
                return;

            case 2:
                std::cout << "Type in your desired password length (max 15)\n";
                std::cin >> length;
                if (length > 15) {
                    length = 15;
                } else if (length < 0) {
                    std::cout << "Password cannot have negative length ;p\n";
                    return;
                }

                while (true) {
                    std::cout << "Should your password have big characters (at least one)? [Y]/[N]\n";
                    std::cin >> yn;
                    if ((yn == "Y") or (yn == "y")) {
                        big = true;
                        break;
                    } else if ((yn == "N") or (yn == "n")) {
                        big = false;
                        break;
                    } else {
                        std::cout << "Please choose correct option!\n";
                    }
                }
                while (true) {
                    std::cout << "Should your password have special characters (at least one)? [Y]/[N]\n";
                    std::cin >> yn;
                    if ((yn == "Y") or (yn == "y")) {
                        special = true;
                        break;
                    } else if ((yn == "N") or (yn == "n")) {
                        special = false;
                        break;
                    } else {
                        std::cout << "Please choose correct option!\n";
                    }
                }
                str = rl.generatePassword(length, special, big);
                std::cout << "Your generated password is: " << str << "\n";
                rl.addPassword(str);
                return;
            default:
                std::cout << "Please choose correct option!\n";

        }
    }
}
void FileManager::listMenu () {
    rl.loadRecords(filePath);
    std::string str;
    int i;

    while (true) {
        std::cout
                << "\n======= [LIST MENU] =======\n1) Search passwords\n2) Sort and display passwords\n3) Add new password\n4) Edit password\n5) Delete password\n6) Add category\n7) Remove category and all of its passwords\n8) Save and close/exit the file\n===========================\nType the correct number to choose a option:\n";
        std::cin >> option;
        checkInput();
        switch (option) {
            case 1:
                std::cout << "\nType in the password to search: \n";
                std::cin >> str;
                listHeader();
                rl.searchList(str);
                break;
            case 2:
                sortMenu();
                break;
            case 3:
                passMenu();
                editMenu(rl.list.size()-1);
                break;
            case 4:
                std::cout << "\nType in id of the password to edit: \n";
                std::cin >> i;
                editMenu(i);
                break;
            case 5:
                std::cout << "\nType in id of the password to delete: \n";
                std::cin >> i;
                rl.deletePassword(i);
                break;
            case 6:
                std::cout << "\nType in the category to add: \n";
                std::cin >> str;
                rl.addCat(str);
                break;
            case 7:
                std::cout << "\nType in the category to remove all of its contents: \n";
                std::cin >> str;
                rl.removeCat(str);
                break;
            case 8:
                std::cout << "\nSaving!";
                rl.saveRecords (filePath);
                return;
            default:
                std::cout << "\nPlease choose correct option!";
        }
    }
}
void FileManager::editMenu(int i) {
    std::string str;
    while (true) {
        std::cout
                << "\n====== [PASSWORD MENU] ======\n1) Edit password\n2) Edit category\n3) Edit login\n4) Edit description\n5) Go back\n=============================\nType the correct number to choose a option:\n";
        listHeader();
        rl.list[i].showRecord(i);
        std::cin >> option;
        checkInput();
        switch (option) {
            case 1:
                std::cout << "Type in your new password: \n";
                std::cin >> str;
                rl.list[i].password = str;
                break;
            case 2:
                std::cout << "Type in your new category: \n";
                std::cin >> str;
                rl.updateCat(i, str);
                break;
            case 3:
                std::cout << "Type in your new login: \n";
                std::cin >> str;
                rl.list[i].login = str;
                break;
            case 4:
                std::cout << "Type in your new description: \n";
                std::getline(std::cin, str);
                std::getline(std::cin, str);
                rl.list[i].description = str;
                break;
            case 5:
                return;
            default:
                std::cout << "\nPlease choose correct option!";
        }
    }
}

void FileManager::sortMenu() {
    while (true) {
        std::cout
                << "\n===== [SORT MENU] =====\n1) Sort by password\n2) Sort by category\n3) Sort by password and category\n======================\nType the correct number to choose a option:\n";
        std::cin >> option;
        checkInput();
        listHeader();
        switch (option) {
            case 1:
                rl.sortList("pass");
                return;
                break;
            case 2:
                rl.sortList("cat");
                return;
                break;
            case 3:
                rl.sortList("passncat");
                return;
                break;
            default:
                std::cout << "\nPlease choose correct option!";
        }
    }
}

void FileManager::checkInput() {
    if (!std::cin)
    {
        std::cin.clear();
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    }
}











