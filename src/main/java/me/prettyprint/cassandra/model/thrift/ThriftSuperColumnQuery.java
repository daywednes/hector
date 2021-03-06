package me.prettyprint.cassandra.model.thrift;

import static me.prettyprint.cassandra.utils.Assert.notNull;
import me.prettyprint.cassandra.model.AbstractSuperColumnQuery;
import me.prettyprint.cassandra.model.HSuperColumn;
import me.prettyprint.cassandra.model.KeyspaceOperationCallback;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.model.Result;
import me.prettyprint.cassandra.model.Serializer;
import me.prettyprint.cassandra.service.Keyspace;
import me.prettyprint.hector.api.exceptions.HNotFoundException;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.query.SuperColumnQuery;

import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.SuperColumn;

/**
 * Thrift implementation of the SuperColumnQuery
 *
 * @author Ran Tavory
 *
 * @param <SN>
 * @param <N>
 * @param <V>
 */
public final class ThriftSuperColumnQuery<SN,N,V> extends AbstractSuperColumnQuery<SN, N, V>
    implements SuperColumnQuery<SN, N, V> {

  /*package*/ public ThriftSuperColumnQuery(KeyspaceOperator keyspaceOperator,
      Serializer<SN> sNameSerializer, Serializer<N> nameSerializer, Serializer<V> valueSerializer) {
    super(keyspaceOperator, sNameSerializer, nameSerializer, valueSerializer);
  }

  @Override
  public Result<HSuperColumn<SN, N, V>> execute() {
    notNull(columnFamilyName, "columnFamilyName is null");
    notNull(superName, "superName is null");
    return new Result<HSuperColumn<SN, N, V>>(keyspaceOperator.doExecute(
        new KeyspaceOperationCallback<HSuperColumn<SN, N, V>>() {
          @Override
          public HSuperColumn<SN, N, V> doInKeyspace(Keyspace ks) throws HectorException {
            try {
              ColumnPath cpath = ThriftFactory.createSuperColumnPath(columnFamilyName, superName, (N) null,
                  sNameSerializer, columnNameSerializer);
              SuperColumn thriftSuperColumn = ks.getSuperColumn(key, cpath);
              if (thriftSuperColumn == null) {
                return null;
              }
              return new HSuperColumn<SN, N, V>(thriftSuperColumn, sNameSerializer, columnNameSerializer,
                  valueSerializer);
            } catch (HNotFoundException e) {
              return null;
            }
          }
        }), this);
  }
}
