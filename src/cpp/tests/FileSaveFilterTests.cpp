#include <gtest/gtest.h>

#include <fstream>
#include <iostream>
#include <vector>
#include <filesystem>
#include "FileSaveFilter.h"

TEST(SaveTests, Save) {
    FileSaveFilter f1("hello.txt");
    std::vector<bool> array = {true, true, false, false, true};
    f1.save(array);

    std::ifstream file("data/hello.txt");
    int i = 0;
    char c;
    while (file.get(c)) {
        if (c == '0') {
            EXPECT_FALSE(array[i]);
        } else if (c == '1') {
            EXPECT_TRUE(array[i]);
        }
        i++;
    }
    file.close();
}

TEST(UploadTests, Upload){
    std::ofstream file1("data/hi.txt");
    file1 << "101010110";
    file1.close();
    FileSaveFilter f2("hi.txt");
    std::vector<bool> array(9,false);
    f2.upload(array);

    std::vector<bool> check = {true, false, true, false, true, false, true, true, false};
    EXPECT_EQ(array, check);
}
