import React from 'react';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';

const roles = [
  'Software Engineer',
  'Product Manager',
  'UI/UX Designer',
  'Recruiter',
  'Intern',
  'VP',
  'Director',
  'CEO'
];

export default function RoleDropdown() {
  const [role, setRole] = React.useState([]);

  const handleChange = (event) => {
    setRole(event.target.value);
  };

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="role">Role</InputLabel>
        <Select
          id="role-input"
          name="role"
          value={role}
          onChange={handleChange}
          inputProps={{ "aria-label": "role" }}
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
