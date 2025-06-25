#include <gtest/gtest.h>
#include <vector>
#include "StandardInput.h"

TEST(IndexTests, Index) {
    StandardInput s;
    std::istringstream fakeInput("1 www.example.com0\n");
    std::streambuf* cinBackup = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput.rdbuf());  // redirect the stdin to our fake input
    s.getValidInput();
    EXPECT_EQ(s.getIndex(), 1);
    std::cin.rdbuf(cinBackup);          // restaure the stdin

    std::istringstream fakeInput1("2 www.example.com0\n");
    std::streambuf* cinBackup1 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput1.rdbuf());  // redirect the stdin to our fake input
    s.getValidInput();
    EXPECT_EQ(s.getIndex(), 2);
    EXPECT_NE(s.getIndex(), 1);
    std::cin.rdbuf(cinBackup1);          // restaure the stdin
}

TEST(URLTests, URLs) {
    StandardInput s1;
    std::istringstream fakeInput2("1 www.example.com0");
    std::streambuf* cinBackup2 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput2.rdbuf());  // redirect the stdin to our fake input
    s1.getValidInput();
    EXPECT_EQ(s1.getURL(), "www.example.com0");
    std::cin.rdbuf(cinBackup2);          // restaure the stdin

    std::istringstream fakeInput3("2 www.example.com1\n");
    std::streambuf* cinBackup3 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput3.rdbuf());  // redirect the stdin to our fake input
    s1.getValidInput();
    EXPECT_EQ(s1.getURL(), "www.example.com1");
    EXPECT_NE(s1.getURL(), "www.example.com0");
    std::cin.rdbuf(cinBackup3);          // restaure the stdin
}

TEST(FirstInputTestSIZE, FirstInputSIZE){
    StandardInput s2;
    std::istringstream fakeInput4("8 1 3");
    std::streambuf* cinBackup4 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput4.rdbuf());  // redirect the stdin to our fake input
    s2.getFirstInput();
    EXPECT_EQ(s2.getSize(), 8);
    EXPECT_NE(s2.getSize(), 5);
    std::cin.rdbuf(cinBackup4);          // restaure the stdin
}

TEST(FirstInputTestFUNC, FirstInputFUNC){
    StandardInput s3;
    std::istringstream fakeInput5("8 1 3");
    std::streambuf* cinBackup5 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput5.rdbuf());  // redirect the stdin to our fake input
    std::vector<int> vec1 = {1,3};
    std::vector<int> vec2 = {4,5};
    s3.getFirstInput();
    EXPECT_EQ(s3.getFunctions(), vec1);
    EXPECT_NE(s3.getFunctions(), vec2);
    std::cin.rdbuf(cinBackup5);          // restaure the stdin
}

TEST(ValidFirstInputTest, ValidFirstInput){
    StandardInput s4;
    std::istringstream fakeInput6("8 1 3");
    std::streambuf* cinBackup6 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput6.rdbuf());  // redirect the stdin to our fake input
    s4.getFirstInput();
    EXPECT_EQ(s4.checkValidFirstInput(), true);
    std::cin.rdbuf(cinBackup6);          // restaure the stdin

    StandardInput s5;
    std::istringstream fakeInput7("0 3 5");
    std::streambuf* cinBackup7 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput7.rdbuf());  // redirect the stdin to our fake input
    s5.getFirstInput();
    EXPECT_EQ(s5.checkValidFirstInput(), false);
    std::cin.rdbuf(cinBackup7);          // restaure the stdin

    StandardInput s6;
    std::istringstream fakeInput8("10");
    std::streambuf* cinBackup8 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput8.rdbuf());  // redirect the stdin to our fake input
    s6.getFirstInput();
    EXPECT_EQ(s6.checkValidFirstInput(), false);
    std::cin.rdbuf(cinBackup8);          // restaure the stdin

    StandardInput s7;
    std::istringstream fakeInput9("8 0 2");
    std::streambuf* cinBackup9 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput9.rdbuf());  // redirect the stdin to our fake input
    s7.getFirstInput();
    EXPECT_EQ(s7.checkValidFirstInput(), false);
    std::cin.rdbuf(cinBackup9);          // restaure the stdin

    StandardInput s8;
    std::istringstream fakeInput10("8 4 5");
    std::streambuf* cinBackup10 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput10.rdbuf());  // redirect the stdin to our fake input
    s8.getFirstInput();
    EXPECT_EQ(s8.checkValidFirstInput(), true);
    std::cin.rdbuf(cinBackup10);          // restaure the stdin

    StandardInput s10;
    std::istringstream fakeInput11("8.5 4 5");
    std::streambuf* cinBackup11 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput11.rdbuf());  // redirect the stdin to our fake input
    s10.getFirstInput();
    EXPECT_EQ(s10.checkValidFirstInput(), false);
    std::cin.rdbuf(cinBackup11);          // restaure the stdin

    StandardInput s11;
    std::istringstream fakeInput12("8 4.5 5");
    std::streambuf* cinBackup12 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput12.rdbuf());  // redirect the stdin to our fake input
    s11.getFirstInput();
    EXPECT_EQ(s11.checkValidFirstInput(), false);
    std::cin.rdbuf(cinBackup12);          // restaure the stdin

    StandardInput s12;
    std::istringstream fakeInput13("8 -4 5");
    std::streambuf* cinBackup13 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput13.rdbuf());  // redirect the stdin to our fake input
    s12.getFirstInput();
    EXPECT_EQ(s12.checkValidFirstInput(), false);
    std::cin.rdbuf(cinBackup13);          // restaure the stdin

    StandardInput s13;
    std::istringstream fakeInput14("-8 4 5");
    std::streambuf* cinBackup14 = std::cin.rdbuf();  // save the stdin
    std::cin.rdbuf(fakeInput14.rdbuf());  // redirect the stdin to our fake input
    s13.getFirstInput();
    EXPECT_EQ(s13.checkValidFirstInput(), false);
    std::cin.rdbuf(cinBackup14);          // restaure the stdin
}

TEST(ValidCommandInputTest, ValidCommandInput){
    StandardInput s9;
    EXPECT_EQ(s9.checkCommand(1), true);
    EXPECT_EQ(s9.checkCommand(3), false);
    EXPECT_EQ(s9.checkCommand(3.4), false);
    EXPECT_EQ(s9.checkURL("hello"), false);
    EXPECT_EQ(s9.checkURL("www.example.com1"), true);
    EXPECT_EQ(s9.checkURL("ww.ex.com"), false);
    EXPECT_EQ(s9.checkURL("www.exam.ple.com1"), true);
    EXPECT_EQ(s9.checkURL("http://www.example.com1"), true);
    EXPECT_EQ(s9.checkURL("www.example."), false);
    EXPECT_EQ(s9.checkURL(""), false);
    EXPECT_EQ(s9.checkURL("\n"), false);
}
