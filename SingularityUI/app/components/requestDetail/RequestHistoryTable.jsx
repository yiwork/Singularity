import React, { PropTypes, Component } from 'react';
import { connect } from 'react-redux';
import { groupBy } from 'underscore';

import Utils from '../../utils';

import { FetchRequestHistory } from '../../actions/api/history';

import Section from '../common/Section';
import { Glyphicon } from 'react-bootstrap';
import UITable from '../common/table/UITable';
import Column from '../common/table/Column';
import JSONButton from '../common/JSONButton';

class RequestHistoryTable extends Component {

  constructor(props) {
    super(props);
    this.state = {
      viewingGroup: null
    };
  }

  renderFolders(groups) {
    return (
      <ul style={{ listStyle: 'none', margin: '25px 0 50px' }}>
        <li>
          <a onClick={() => this.setState({ viewingGroup: 'All Users' })}>
            <Glyphicon glyph="folder-open" />
            <span className="file-name">All Users</span>
          </a>
        </li>
        <li>
          <a onClick={() => this.setState({ viewingGroup: 'Human Users' })}>
            <Glyphicon glyph="folder-open" />
            <span className="file-name">Human Users</span>
          </a>
        </li>
        <li>
          <a onClick={() => this.setState({ viewingGroup: 'Automation Users' })}>
            <Glyphicon glyph="folder-open" />
            <span className="file-name">Automation Users</span>
          </a>
        </li>
      </ul>
    );
  }

  renderTable() {
    const {requestId, requestEventsAPI, fetchRequestHistory} = this.props;
    const requestEvents = requestEventsAPI ? requestEventsAPI.data : [];
    const isFetching = requestEventsAPI ? requestEventsAPI.isFetching : false;
    const emptyTableMessage = (Utils.api.isFirstLoad(requestEventsAPI)
      ? 'Loading...'
      : 'No request history'
    );
    return (
      <div>
        <h5>
          <a onClick={() => this.setState({ viewingGroup: null })}>
            <Glyphicon glyph="chevron-left" />
            <span className="file-name">Back</span>
          </a>
          <span className="file-name">{this.state.viewingGroup}</span>
        </h5>
        <UITable
          emptyTableMessage={emptyTableMessage}
          data={requestEvents}
          keyGetter={(requestEvent) => requestEvent.createdAt}
          rowChunkSize={5}
          paginated={true}
          fetchDataFromApi={(page, numberPerPage) => fetchRequestHistory(requestId, numberPerPage, page)}
          isFetching={isFetching}
        >
          <Column
            label="State"
            id="state"
            key="state"
            cellData={(requestEvent) => Utils.humanizeText(requestEvent.eventType)}
          />
          <Column
            label="User"
            id="user"
            key="user"
            cellData={(requestEvent) => (requestEvent.user || '').split('@')[0]}
          />
          <Column
            label="Timestamp"
            id="timestamp"
            key="timestamp"
            cellData={(requestEvent) => Utils.timestampFromNow(requestEvent.createdAt)}
          />
          <Column
            label="Message"
            id="message"
            key="message"
            cellData={(requestEvent) => requestEvent.message}
          />
          <Column
            id="actions-column"
            key="actions-column"
            className="actions-column"
            cellData={(requestEvent) => <JSONButton object={requestEvent} showOverlay={true}>{'{ }'}</JSONButton>}
          />
        </UITable>
      </div>
    );
  }

  getEventType(event) {
    console.log('2')
    console.log(event['user'])
    if (config.automationUsers.includes(event['user'])) {
      console.log('1')
      return 'Automation Users';
    } else {
      return 'Human Users';
    }
  }

  render() {
    const {requestId, requestEventsAPI, fetchRequestHistory} = this.props;
    const requestEvents = requestEventsAPI ? requestEventsAPI.data : [];
    const isFetching = requestEventsAPI ? requestEventsAPI.isFetching : false;
    const emptyTableMessage = (Utils.api.isFirstLoad(requestEventsAPI)
      ? 'Loading...'
      : 'No request history'
    );

    if (requestEvents) {
      const groupedHistory = groupBy(requestEvents, this.getEventType);

      return (
        <Section id="request-history" title="Request history">
          {this.state.viewingGroup
            ? this.renderTable(this.state.viewingGroup === 'All Users' ? requestEvents : groupedHistory[this.state.viewingGroup])
            : this.renderFolders(Object.keys(groupedHistory))
          }
        </Section>
      );
    } else {
      return null;
    }
  }
};

RequestHistoryTable.propTypes = {
  requestId: PropTypes.string.isRequired,
  requestEventsAPI: PropTypes.object.isRequired,
  fetchRequestHistory: PropTypes.func.isRequired
};

const mapDispatchToProps = (dispatch) => ({
  fetchRequestHistory: (requestId, count, page) => dispatch(FetchRequestHistory.trigger(requestId, count, page))
});

const mapStateToProps = (state, ownProps) => ({
  requestEventsAPI: Utils.maybe(
    state.api.requestHistory,
    [ownProps.requestId]
  )
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(RequestHistoryTable);
