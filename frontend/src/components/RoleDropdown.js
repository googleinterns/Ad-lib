import React from 'react';
import PropTypes from 'prop-types';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';

// TO-DO: Add more role options to this array
const roles = [
  'Software Engineer',
  'Product Manager',
  'UI/UX Designer',
  'Recruiter',
  'Intern',
  'VP',
  'Director',
  'CEO',
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
            <MenuItem key={currentRole} value={currentRole}>
              <ListItemText primary={currentRole} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
