#!/usr/bin/env python2
"""The fake-core binary of your application

Assume this is the core binary of your application. Assume your goal
is to keep this binary running without any problems. The purpose is
to train you using `git bisection` to find out which commit causes
this binary stopped to work.

The algorithm of this script is to find out how many lines of
comments are there in the repo. If it exceeds the number we pass
to this program, it'll raise an exception.
"""

from __future__ import print_function

import argparse
import os
import sys

COMMENT_STYLES = {
    '.java': ['//', ('/*', '*/')],
    '.js': ['//', ('/*', '*/')],
    '.html': [('<!--', '-->')],
    '.xml': [('<!--', '-->')],
}


def count_comments_from_contents(contents, style):
    lines = 0
    if isinstance(style, str):
        # Consider single line comments
        for line in contents.split('\n'):
            if line.strip().startswith(style):
                lines += 1
        return lines

    if isinstance(style, tuple):
        # Consider multi-line comments
        begin, end = style
        in_comment_section = False
        for line in contents.split('\n'):
            if line.strip().startswith(begin):
                lines += 1
                in_comment_section = True
            elif line.strip().startswith(end):
                lines += 1
                in_comment_section = False
            elif in_comment_section:
                lines += 1
        return lines


def count_comments(path):
    for ext, styles in COMMENT_STYLES.items():
        if path.endswith(ext):
            comment_lines = 0
            with open(path) as f:
                contents = f.read()
            for s in styles:
                comment_lines += count_comments_from_contents(contents, s)

            if comment_lines:
                print('Found %d lines of comments in %s' %
                      (comment_lines, path))
            return comment_lines
    assert '%s not ending with any supported file extensions' % path


def collect_programs(threshold_lines):
    directory = os.path.abspath(os.path.dirname(__file__))
    total_lines = 0
    for top in [
            os.path.join(directory, '../frontend'),
            os.path.join(directory, '../backend')
    ]:
        for root, _, files in os.walk(top):
            for f in files:
                if f.endswith(tuple(COMMENT_STYLES.keys())):
                    total_lines += count_comments(os.path.join(root, f))
    if total_lines > threshold_lines:
        raise RuntimeError(
            'Oh no!!!! You have %d lines of comments, exceeding the threshold of %d'
            % (total_lines, threshold_lines))
    print(
        'You have total of %d lines of comments in all %s files in the repo' %
        (total_lines, COMMENT_STYLES.keys()))


def main(argv):
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--number',
        type=int,
        default=1000,
        help=(
            'The "ideal" number of comments in all of these repo. '
            'Exceeding this number will cause this script to raise an error. '
            'Default is %(default)s'))

    options = parser.parse_args(argv)
    collect_programs(options.number)


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
