// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';

/**
 * Establishes style to use on rendering components
 */
const useStyles = makeStyles((theme) => ({
  content: {
    margin: theme.spacing(2),
    width: 800,
  },
}));

/**
 * Define NoMatchPage component
 * @param {Object} props
 * @return {NoMatchPage} NoMatchPage component
 */
export default function NoMatchPage(props) {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>Sorry, We could not find you a match :(</h3>
          {/* TO-DO(#78): Display more personalized info on page*/}
          <p>It looks like you are only free until 11:45am, and we could not
             find you a match to meet or 30 minutes before then. Please try
             again later, and happy working!</p>
        </CardContent>
      </Card>
    </div>
  );
}
