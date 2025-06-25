#ifndef ONEEXECUTOR_H
#define ONEEXECUTOR_H

#include <iostream>
#include <functional>
#include "IExecutor.h"

class oneExecutor : public IExecutor {
public:
    void submit(std::function<void()> task);
    ~oneExecutor() = default;
};

#endif 