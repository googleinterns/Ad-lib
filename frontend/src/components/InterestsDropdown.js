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
import Input from '@material-ui/core/Input';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';

const interestsList = [
  'Anime/Manga',
  'Art & Design',
  'Books',
  'Current Events',
  'Entertainment',
  'Exercise/Fitness',
  'Food',
  'Gaming',
  'Mental Health',
  'Mindfulness',
  'Music',
  'Nature',
  'Pets',
  'Public Speaking',
  'Shopping',
  'Social Activism',
  'Sports',
  'Travel',
  'Volunteering',
  'Writing',
];

// Add onChange to props validation
InterestsDropdown.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create interests dropdown component using interests array
 * @param {Object} props
 * @return {InterestsDropdown} InterestsDropdown component
 */
export default function InterestsDropdown(props) {
  const [interests, interestsSelected] = React.useState([]);

  const handleChange = (event) => {
    const interest = event.target.value;
    props.onChange(interest);
    interestsSelected(interest);
  };

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="interests">Interests</InputLabel>
        <Select
          id="interests"
          name="interests"
          multiple
          value={interests}
          onChange={handleChange}
          input={<Input />}
          renderValue={(selected) => selected.join(', ')}
          inputProps={{'aria-label': 'interests'}}
        >
          {interestsList.map((currentInterest) => (
            <MenuItem
              data-testid="menu-option"
              key={currentInterest}
              value={currentInterest}
            >
              <ListItemText primary={currentInterest} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
