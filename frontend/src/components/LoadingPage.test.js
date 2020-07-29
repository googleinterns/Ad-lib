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
import mockAxios from 'axios';
import LoadingPage from './LoadingPage';
import renderer from 'react-test-renderer';
import {sendPostRequest} from './LoadingPage';

jest.mock('axios');

describe('Loading Page', () => {
  it('should be defined', () => {
    expect(LoadingPage).toBeDefined();
  });

  it('should render correctly', () => {
    const tree = renderer.create(<LoadingPage />).toJSON();
    expect(tree).toMatchSnapshot();
  });

  it('POST request using axios to servlet with form details', () => {
    const servletEndpoint = '/api/v1/remove-participant';
    const removeParticipantRequest = 'Remove Participant';

    sendPostRequest();

    expect(mockAxios.post).toHaveBeenCalledTimes(1);
    expect(mockAxios.post).toHaveBeenCalledWith(
        servletEndpoint, {removeParticipantRequest},
    );
  });
});
