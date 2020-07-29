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
import PropTypes from 'prop-types';
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

// Add matchInfomation to props validation
MatchPage.propTypes = {
  matchInformation: PropTypes.string,
};

/**
 * Define MatchPage component
 * @param {Object} props
 * @return {MatchPage} MatchPage component
 */
export default function MatchPage(props) {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>We found you a match!</h3>
          <p>{props.matchInformation} is so excited to meet you!</p>
          <p>Please check your Calendar to find the event and Meet link and
          join to meet your new friend.</p>
        </CardContent>
      </Card>
    </div>
  );
}
