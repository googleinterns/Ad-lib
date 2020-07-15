import React from 'react';
import DurationDropdown from './DurationDropdown';
import renderer from 'react-test-renderer';

describe('Duration Dropdown', () => {
  it('should be defined', () => {
    expect(DurationDropdown).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<DurationDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});  