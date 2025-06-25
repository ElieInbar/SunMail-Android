#include <gtest/gtest.h>

#include <fstream>
#include <iostream>
#include <vector>
#include <filesystem>
#include <string>
#include <unordered_set>


#include "FileSaveURL.h"

TEST(SaveTestsURL, SaveURL) {
    FileSaveURL f1("hello.txt");
    std::unordered_set<std::string> array = {"www.exam.com", "www.hello.com2", "www.coca.fr"};
    f1.save(array);

    std::ifstream file("data/hello.txt");
    std::string line;
    while (getline(file, line)) {
        EXPECT_TRUE(array.count(line));
    }
    file.close();
}

TEST(UploadTestsURL, UploadURL){
    std::ofstream file1("data/hi.txt");
    file1 << "www.examp.com" << std::endl << "www.hellooo.com2" << std::endl << "www.cocacola.fr";
    file1.close();
    FileSaveURL f2("hi.txt");
    std::unordered_set<std::string> array;
    f2.upload(array);
    
    std::unordered_set<std::string> check = {"www.examp.com", "www.hellooo.com2", "www.cocacola.fr"};
    EXPECT_EQ(array, check);
}
