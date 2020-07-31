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
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';

// Add onChange and shouldDisableMatchPreferenceFields to props validation
MatchPreference.propTypes = {
  onChange: PropTypes.func,
  shouldDisableMatchPreferenceFields: PropTypes.bool,
  value: PropTypes.string,
};

/**
 * Create match preference radio group component
 * @param {Object} props
 * @return {MatchPreference} MatchPreference component
 */
export default function MatchPreference(props) {
  return (
    <FormControl component="fieldset">
      <RadioGroup
        row
        aria-label="matchPreference"
        name="matchPreference"
        value={props && props.value ? props.value : 'any'}
        onChange={(event) => props.onChange(event.target.value)}
      >
        <FormControlLabel
          value="similar"
          disabled={props.shouldDisableMatchPreferenceFields}
          control={<Radio color="primary" />}
          label="Similar Googler" />
        <FormControlLabel
          value="any"
          control={<Radio color="primary" />}
          label="Any Googler" />
        <FormControlLabel
          value="different"
          disabled={props.shouldDisableMatchPreferenceFields}
          control={<Radio color="primary" />}
          label="Different Googler" />
      </RadioGroup>
      <FormHelperText>You must select at least one personal preference
        field to select a match preference </FormHelperText>
    </FormControl>
  );
}
