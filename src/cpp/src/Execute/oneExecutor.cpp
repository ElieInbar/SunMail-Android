#include "IExecutor.h"
#include "oneExecutor.h"
#include <iostream>

void oneExecutor::submit(std::function<void()> task) {
    task();
}
