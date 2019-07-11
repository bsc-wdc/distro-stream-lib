#!/usr/bin/python

# -*- coding: utf-8 -*-

# For better print formatting
from __future__ import print_function


#
# HELPER CLASS
#

class MyElement(object):

    def __init__(self, input_file):
        self.input_files = [input_file]
        self.processed = False

    def add_inputs(self, input_files):
        self.input_files.extend(input_files)

    def set_processed(self):
        self.processed = True
