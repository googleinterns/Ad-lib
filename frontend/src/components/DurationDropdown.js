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
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

/** Establishes style to use on rendering component */
const useStyles = makeStyles((theme) => ({
  padding: {
    margin: theme.spacing(2),
    marginBottom: theme.spacing(3),
  },
}));

const durations = [
  {label: '15 minutes', value: 15},
  {label: '30 minutes', value: 30},
  {label: '45 minutes', value: 45},
  {label: '60 minutes', value: 60},
];

// Add onChange to props validation
DurationDropdown.propTypes = {
  onChange: PropTypes.func,
  value: PropTypes.string,
};

/**
 * Create duration dropdown component using durations array
 * @param {Object} props
 * @return {DurationDropdown} DurationDropdown component
 */
export default function DurationDropdown(props) {
  const classes = useStyles();

  return (
    <div>
      <FormControl style={{width: 180}}>
        <Select
          id="duration-input"
          name="duration"
          value={props && props.value ? props.value : 15}
          onChange={(event) => props.onChange(event.target.value)}
          inputProps={{'aria-label': 'duration'}}
        >
          {durations.map((currentDuration) => (
            <option
              className={classes.padding}
              key={currentDuration.label}
              value={currentDuration.value}
            >
              {currentDuration.label}
            </option>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
