#include "ThreadExecutor.h"


void ThreadExecutor::submit(std::function<void()> task) {
    std::thread t(task);
    t.detach();
}
