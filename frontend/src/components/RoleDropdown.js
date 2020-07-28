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
import InputLabel from '@material-ui/core/InputLabel';
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

const roles = [
  'Accountant',
  'Administrative',
  'Analyst',
  'Attorney',
  'Business strategy consultant',
  'Communications',
  'Coordinator',
  'Corporate development/M&A',
  'Corporate engineer',
  'Creative editorial',
  'Data warehousing',
  'Developer relations',
  'General program manager',
  'Hardware',
  'HR professional',
  'Learning and development',
  'Legal support',
  'Marketing',
  'Network engineer and ops',
  'Ops - business processes',
  'Ops - data center',
  'Ops - hardware',
  'Ops - supply chain and manufacturing',
  'Partnerships and business development',
  'Physical security',
  'Policy',
  'Product manager',
  'Quant',
  'Real estate',
  'Research scientist',
  'Sales - account executive',
  'Sales - account management',
  'Sales - enterprise',
  'Sales - new client acquisition',
  'Sales - services',
  'Security engineer',
  'Site reliability engineer',
  'Software engineer',
  'Software engineer, tools and infrastructure',
  'Specialty roles',
  'Staffing',
  'System integrator',
  'Technical client facing',
  'Technical program manager',
  'Technical writer',
  'User experience',
  'Web developer',
  'Workplace services',
];

// Add onChange to props validation
RoleDropdown.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create role dropdown component using roles array
 * @param {Object} props
 * @return {RoleDropdown} RoleDropdown component
 */
export default function RoleDropdown(props) {
  const classes = useStyles();

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="role">Role</InputLabel>
        <Select
          id="role-input"
          name="role"
          onChange={(event) => props.onChange(event.target.value)}
          inputProps={{'aria-label': 'role'}}
        >
          {roles.map((currentRole) => (
            <option
              className={classes.padding}
              key={currentRole}
              value={currentRole}
            >
              {currentRole}
            </option>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
