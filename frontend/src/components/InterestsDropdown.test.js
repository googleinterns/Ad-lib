import React from 'react';
import InterestsDropdown from './InterestsDropdown';
import renderer from 'react-test-renderer';

describe('Interests Dropdown', () => {
  it('should be defined', () => {
    expect(InterestsDropdown).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<InterestsDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});
