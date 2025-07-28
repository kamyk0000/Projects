cmake_minimum_required(VERSION 3.23)
project(Project2)

set(CMAKE_CXX_STANDARD 23)

add_executable(Project2 main.cpp FileManager.cpp FileManager.h Record.cpp Record.h)
