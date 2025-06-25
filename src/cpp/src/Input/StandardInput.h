#ifndef STANDARDINPUT_H
#define STANDARDINPUT_H

#include <string>
#include <vector>

#include "IInput.h"

class StandardInput : public IInput {
    int m_index;
    std::string m_URL;
    // a vector that will store the first input: the size of the array and the
    // functions
    std::vector<int> sizeAndFunctions;

   public:
    int getIndex() override;
    std::string getURL() const override;
    // get the command input from the user and check if it's valid
    bool getValidInput() override;
    // get the first input from the user and check if it's valid
    bool getFirstInput();
    int getSize();
    std::vector<int> getFunctions();
    // check if the first input is valid
    bool checkValidFirstInput();
    bool checkURL(std::string url);
    bool checkCommand(int index);
    std::string getCommand() override;

   private:
    bool isNumber(std::string str);
    void setIndex(int index1);
    void setURL(std::string url);
};

#endif
