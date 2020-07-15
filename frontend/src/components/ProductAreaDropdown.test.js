import React from 'react';
import ProductAreaDropdown from './ProductAreaDropdown';
import renderer from 'react-test-renderer';

describe('Product Area Dropdown', () => {
  it('should be defined', () => {
    expect(ProductAreaDropdown).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<ProductAreaDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});
