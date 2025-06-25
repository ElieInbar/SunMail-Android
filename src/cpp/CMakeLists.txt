cmake_minimum_required(VERSION 3.10)
project(BloomFilterProject)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(
    ${CMAKE_SOURCE_DIR}/src
    ${CMAKE_SOURCE_DIR}/src/App
    ${CMAKE_SOURCE_DIR}/src/BloomFilter
    ${CMAKE_SOURCE_DIR}/src/Command
    ${CMAKE_SOURCE_DIR}/src/Hash
    ${CMAKE_SOURCE_DIR}/src/Input
    ${CMAKE_SOURCE_DIR}/src/Naive
    ${CMAKE_SOURCE_DIR}/src/Save
    ${CMAKE_SOURCE_DIR}/src/Server
    ${CMAKE_SOURCE_DIR}/src/Execute
)

# Gather all .cpp and .h files
file(GLOB_RECURSE ALL_SOURCES "src/*.cpp" "src/*.h")

# === Shared Sources ===
set(LIB_SOURCES ${ALL_SOURCES})
list(FILTER LIB_SOURCES EXCLUDE REGEX "src/tests/.*")

list(FILTER LIB_SOURCES EXCLUDE REGEX "src/Server/Server.cpp") # Exclure Main.cpp pour tests

# === Main executable (App) ===
add_executable(app ${LIB_SOURCES} src/Server/Server.cpp)

# === Unit tests ===
if (BUILD_TESTING)
    enable_testing()
    find_package(GTest REQUIRED)
    include_directories(${GTEST_INCLUDE_DIRS})

    file(GLOB TEST_SOURCES "src/tests/*.cpp")

    add_executable(tests ${LIB_SOURCES} ${TEST_SOURCES})
    target_link_libraries(tests ${GTEST_LIBRARIES} pthread)

    add_test(NAME AllTests COMMAND tests)
endif()
