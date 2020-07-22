import React from 'react';
import InterestsDropdown from './InterestsDropdown';
import renderer from 'react-test-renderer';

describe('Interests Dropdown', () => {
  it('should be defined', () => {
    expect(InterestsDropdown).toBeDefined();
  });
  it('should be populated with interests in alaphabetical order', () => {
    const tree = renderer.create(<InterestsDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});
