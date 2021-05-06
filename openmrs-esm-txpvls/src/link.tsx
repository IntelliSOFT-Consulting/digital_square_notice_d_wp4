import React from 'react';
import { ConfigurableLink } from '@openmrs/esm-react-utils';

export default () => (
  <ConfigurableLink to="${openmrsSpaBase}/home" className="bx--side-nav__link">
    TxPVLS Report
  </ConfigurableLink>
);
