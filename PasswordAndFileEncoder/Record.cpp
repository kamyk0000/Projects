#include <fstream>
#include <iostream>
#include <sstream>
#include <regex>
#include <iomanip>
#include "Record.h"
#include "FileManager.h"
Record::Record(std::string pass) {
    password=pass;
    category="---";
    login="---";
    description="---";
}
Record::Record(std::string pass, std::string cat, std::string log, std::string desc) {
    password=pass;
    category=cat;
    login=log;
    description=desc;
}
bool RecordList::checkIndex (int i) {
    if (i<0 || i>list.size()-1) {
        std::cout << "Sorry no records found on this index...";
        return false;
    }
    return true;
}
void Record::showRecord (int i) {
    std::string id = "[" + std::to_string(i) + "]";
    std::cout << std::left << std::setw(5) << id
              << std::left << std::setw(25) << password
              << std::left << std::setw(20) << category
              << std::left << std::setw(20) << login
              << std::left << std::setw(5) << description << "\n";
}
void RecordList::loadRecords(const std::string& path) {
    std::vector<std::string> strs;
    std::fstream fin;
    fin.open(path, std::ios::in);
    std::string line, split;

    std::getline(fin, line); //skipline
    while (std::getline(fin, line)) {
        std::stringstream stream(line);
        while(std::getline(stream, split, s))
        {
            strs.push_back(split);
        }
        list.push_back(*new Record (strs[0], strs[1], strs[2], strs[3]));
        categories.push_back(strs[1]);
        strs.clear();
    }
    fin.close();
    sort( categories.begin(), categories.end() );
    categories.erase( unique( categories.begin(), categories.end() ), categories.end() );
}
void RecordList::saveRecords(const std::string& path) {
    std::fstream fout;

    fout.open(path, std::ios::out);
    fout << "ph" << "\n";
    for (const Record& rec:list) {
        fout    << rec.password << s
                << rec.category << s
                << rec.login << s
                << rec.description << "\n";
    }
    fout.close();
    list.clear();
}
void RecordList::searchList (const std::string& str) {
    std::vector<Record> listCopy=list;
    std::regex reg(".*"+str+".*");
    int i = 0,y = list.size()-1;

    for (Record &record : list) {
        if (std::regex_match(record.password, reg)){
            record.showRecord(i);
            listCopy[i]=record;
            i++;
        } else {
            listCopy[y]=record;
            y--;
        }
    }
    list=listCopy;
    if (i==0) {
        std::cout << "Sorry no matching passwords found...";
    }
}
void RecordList::sortList (std::string by) {

    if (by == "pass"){
        std::sort(list.begin(),list.end(),[](Record &a, Record &b){ return a.password < b.password; });
    } else if (by == "cat" ) {
        std::sort(list.begin(),list.end(),[](Record &a, Record &b){ return a.category < b.category; });
    } else {
        std::sort(list.begin(),list.end(),[](Record &a, Record &b)
        { if(a.password != b.password) {return a.password < b.password;}else return a.category < b.category;});
    }

    int i=0;
    for (Record &record : list) {
        record.showRecord(i);
        i++;
    }
}

void RecordList::deletePassword(int id){
    if (checkIndex(id)){
        list.erase(list.begin()+id);
    }
}
bool RecordList::isDuplicate (const std::string& toCheck) {
    for (const Record& record:list) {
        if (record.password==toCheck)
            return true;
    }
    return false;
}
void RecordList::addPassword(const std::string& password) {
    list.push_back(*new Record (password));
}
std::string RecordList::generatePassword (int length, bool special, bool big) {
    const char normalChars[] = "abcdefghijklmnopqrstuvwxyz";
    const char bigChars[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const char bigNormalChars[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    const char specialChars[] = "0123456789!@#$%^&*";
    const char specialNormalChars[] = "0123456789!@#$%^&*abcdefghijklmnopqrstuvwxyz";
    const char allChars[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";

    srand(time(nullptr));

    std::string pass;
    char build[length];

    while (true) {
        if (!special and !big) {
            for (int i = 0; i < length; ++i) {
                build[i] = normalChars[rand() % sizeof(normalChars)];
            }
        } else if (!special and big) {
            for (int i = 0; i < length; ++i) {
                build[i] = bigNormalChars[rand() % sizeof(bigNormalChars)];
            }
            build[0] = bigChars[rand() % sizeof(bigChars)];
        } else if (special and !big) {
            for (int i = 0; i < length; ++i) {
                build[i] = specialNormalChars[rand() % sizeof(specialNormalChars)];
            }
            build[length - 1] = specialChars[rand() % sizeof(specialChars)];
        } else {
            for (int i = 0; i < length; ++i) {
                build[i] = allChars[rand() % sizeof(allChars)];
            }
            build[0] = bigChars[rand() % sizeof(bigChars)];
            build[length - 1] = specialChars[rand() % sizeof(specialChars)];
        }
        for (int i = 0; i < length; ++i) {
            pass += build[i];
        }
        if (!isDuplicate(pass)){
            break;
        }
    }
    return pass;
}
bool RecordList::isDupeCat(const std::string& toCheck) {
    for (const std::string& cat:categories) {
        if (cat==toCheck)
            return true;
    }
    return false;
}
void RecordList::addCat(const std::string& cat) {
    categories.push_back(cat);
}
void RecordList::removeCat(const std::string& cat) {
    for (int i = 0; i < list.size(); ++i) {
        if (list[i].category == cat) {
            list.erase(list.begin() + i);
        }
    }
    for (int i = 0; i < categories.size(); ++i) {
        if (categories[i] == cat) {
            categories.erase(categories.begin() + i);
        }
    }
}
void RecordList::updateCat(int i, const std::string& cat) {
    list[i].category=cat;
    if (!isDupeCat(cat)){
        addCat(cat);
    }
}


