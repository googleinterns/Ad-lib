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
  '15-20 years',
  '20+ years'
];

export default function YearDropdown() {
  const [yearRange, setYearRange] = React.useState([]);

  const handleChange = (event) => {
    setYearRange(event.target.value);
  };

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="year-range">Years@</InputLabel>
        <Select
          id="year-range-input"
          name="year-range"
          value={yearRange}
          onChange={handleChange}
          inputProps={{ "aria-label": "year-range" }}
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
