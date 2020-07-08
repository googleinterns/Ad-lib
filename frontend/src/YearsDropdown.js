import React from 'react';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';

const yearRanges = [
  '0-5 years',
  '6-10 years',
  '11-15 years',
  '16-20 years',
  '20+ years',
];

export default function YearDropdown(props) {
  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="year-range">Years@</InputLabel>
        <Select
          id="year-range-input"
          name="year-range"
          onChange={(event) => props.onChange(event.target.value)}
          inputProps={{'aria-label': 'year-range'}}
        >
          {yearRanges.map((currentYearRange) => (
            <MenuItem key={currentYearRange} value={currentYearRange}>
              <ListItemText primary={currentYearRange} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
