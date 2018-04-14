package ru.ifmo.ctddev.semenov.sd.catalog

import com.mongodb.*
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.*
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.rx.client.*
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import rx.Observable
import rx.Subscriber
import rx.Subscription
import java.util.concurrent.TimeUnit

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
internal class MongoDBTest {
    lateinit var mongo: MongoDB

    @BeforeEach
    fun setUp() {
        val users = MongoCollectionImpl()
        val currencies = MongoCollectionImpl()
        currencies.insertOne(Currency("RUR", 61.7).toDocument())
        currencies.insertOne(Currency("USD", 1.0).toDocument())
        val products = MongoCollectionImpl()
        mongo = MongoDB(users, products, currencies)
    }

    @Test
    fun testConversion() {
        val petya = User("petya", "petya@ya.ru", "RUR")
        val john = User("john", "john@ya.ru", "USD")
        mongo.registerUser(petya)
        mongo.registerUser(john)
        val price = 100.0
        mongo.addProduct(Product("Test", "test description", price)).subscribe()
        mongo.getProductsForUser(petya.email).subscribe { product ->
            assertTrue(Math.abs(product.price - price * 61.7) < 1e-7)
        }
        mongo.getProductsForUser(john.email).subscribe { product ->
            assertTrue(Math.abs(product.price - price) < 1e-7)
        }
    }
}

// Mocks

class MongoCollectionImpl : MongoCollection<Document> {
    private val list = arrayListOf<Document>()
    override fun withObservableAdapter(observableAdapter: ObservableAdapter?): MongoCollection<Document> {
        TODO("not implemented")
    }

    override fun findOneAndReplace(filter: Bson?, replacement: Document?): Observable<Document> {
        TODO("not implemented")
    }

    override fun findOneAndReplace(filter: Bson?, replacement: Document?, options: FindOneAndReplaceOptions?): Observable<Document> {
        TODO("not implemented")
    }

    override fun drop(): Observable<Success> {
        TODO("not implemented")
    }

    override fun renameCollection(newCollectionNamespace: MongoNamespace?): Observable<Success> {
        TODO("not implemented")
    }

    override fun renameCollection(newCollectionNamespace: MongoNamespace?, options: RenameCollectionOptions?): Observable<Success> {
        TODO("not implemented")
    }

    override fun deleteMany(filter: Bson?): Observable<DeleteResult> {
        TODO("not implemented")
    }

    override fun deleteMany(filter: Bson?, options: DeleteOptions?): Observable<DeleteResult> {
        TODO("not implemented")
    }

    override fun deleteOne(filter: Bson?): Observable<DeleteResult> {
        TODO("not implemented")
    }

    override fun deleteOne(filter: Bson?, options: DeleteOptions?): Observable<DeleteResult> {
        TODO("not implemented")
    }

    override fun aggregate(pipeline: MutableList<out Bson>?): AggregateObservable<Document> {
        TODO("not implemented")
    }

    override fun <TResult : Any?> aggregate(pipeline: MutableList<out Bson>?, clazz: Class<TResult>?): AggregateObservable<TResult> {
        TODO("not implemented")
    }

    override fun replaceOne(filter: Bson?, replacement: Document?): Observable<UpdateResult> {
        TODO("not implemented")
    }

    override fun replaceOne(filter: Bson?, replacement: Document?, options: UpdateOptions?): Observable<UpdateResult> {
        TODO("not implemented")
    }

    override fun count(): Observable<Long> {
        TODO("not implemented")
    }

    override fun count(filter: Bson?): Observable<Long> {
        TODO("not implemented")
    }

    override fun count(filter: Bson?, options: CountOptions?): Observable<Long> {
        TODO("not implemented")
    }

    override fun getObservableAdapter(): ObservableAdapter {
        TODO("not implemented")
    }

    override fun createIndex(key: Bson?): Observable<String> {
        TODO("not implemented")
    }

    override fun createIndex(key: Bson?, options: IndexOptions?): Observable<String> {
        TODO("not implemented")
    }

    override fun withWriteConcern(writeConcern: WriteConcern?): MongoCollection<Document> {
        TODO("not implemented")
    }

    override fun mapReduce(mapFunction: String?, reduceFunction: String?): MapReduceObservable<Document> {
        TODO("not implemented")
    }

    override fun <TResult : Any?> mapReduce(mapFunction: String?, reduceFunction: String?, clazz: Class<TResult>?): MapReduceObservable<TResult> {
        TODO("not implemented")
    }

    override fun updateOne(filter: Bson?, update: Bson?): Observable<UpdateResult> {
        TODO("not implemented")
    }

    override fun updateOne(filter: Bson?, update: Bson?, options: UpdateOptions?): Observable<UpdateResult> {
        TODO("not implemented")
    }

    override fun insertMany(documents: MutableList<out Document>?): Observable<Success> {
        TODO("not implemented")
    }

    override fun insertMany(documents: MutableList<out Document>?, options: InsertManyOptions?): Observable<Success> {
        TODO("not implemented")
    }

    override fun dropIndexes(): Observable<Success> {
        TODO("not implemented")
    }

    override fun <NewTDocument : Any?> withDocumentClass(clazz: Class<NewTDocument>?): MongoCollection<NewTDocument> {
        TODO("not implemented")
    }

    override fun findOneAndUpdate(filter: Bson?, update: Bson?): Observable<Document> {
        TODO("not implemented")
    }

    override fun findOneAndUpdate(filter: Bson?, update: Bson?, options: FindOneAndUpdateOptions?): Observable<Document> {
        TODO("not implemented")
    }

    override fun withCodecRegistry(codecRegistry: CodecRegistry?): MongoCollection<Document> {
        TODO("not implemented")
    }

    override fun insertOne(document: Document?): Observable<Success> {
        list.add(document!!)
        return Observable.just(Success.SUCCESS)
    }

    override fun insertOne(document: Document?, options: InsertOneOptions?): Observable<Success> {
        TODO("not implemented")
    }

    override fun getDocumentClass(): Class<Document> {
        TODO("not implemented")
    }

    override fun find(): FindObservable<Document> {
        return if (list.isEmpty()) FindObservableImpl.empty() else FindObservableImpl.of(list[0])
    }

    override fun <TResult : Any?> find(clazz: Class<TResult>?): FindObservable<TResult> {
        TODO("not implemented")
    }

    override fun find(filter: Bson?): FindObservable<Document> {
        val doc = filter as Document
        val (key, value) = when {
            doc.containsKey(EMAIL_KEY)    -> Pair("email", doc[EMAIL_KEY])
            doc.containsKey(CURRENCY_KEY) -> Pair("code", doc[CURRENCY_KEY])
            else                                  -> throw IllegalStateException("Unsupported doc: $doc")
        }
        for (elem in list) {
            if (elem.containsKey(key) && elem[key] == value) {
                return FindObservableImpl.of(elem)
            }
        }
        return FindObservableImpl.empty()
    }

    override fun <TResult : Any?> find(filter: Bson?, clazz: Class<TResult>?): FindObservable<TResult> {
        TODO("not implemented")
    }

    override fun dropIndex(indexName: String?): Observable<Success> {
        TODO("not implemented")
    }

    override fun dropIndex(keys: Bson?): Observable<Success> {
        TODO("not implemented")
    }

    override fun getCodecRegistry(): CodecRegistry {
        TODO("not implemented")
    }

    override fun withReadConcern(readConcern: ReadConcern?): MongoCollection<Document> {
        TODO("not implemented")
    }

    override fun createIndexes(indexes: MutableList<IndexModel>?): Observable<String> {
        TODO("not implemented")
    }

    override fun listIndexes(): ListIndexesObservable<Document> {
        TODO("not implemented")
    }

    override fun <TResult : Any?> listIndexes(clazz: Class<TResult>?): ListIndexesObservable<TResult> {
        TODO("not implemented")
    }

    override fun withReadPreference(readPreference: ReadPreference?): MongoCollection<Document> {
        TODO("not implemented")
    }

    override fun getNamespace(): MongoNamespace {
        TODO("not implemented")
    }

    override fun updateMany(filter: Bson?, update: Bson?): Observable<UpdateResult> {
        TODO("not implemented")
    }

    override fun updateMany(filter: Bson?, update: Bson?, options: UpdateOptions?): Observable<UpdateResult> {
        TODO("not implemented")
    }

    override fun <TResult : Any?> distinct(fieldName: String?, resultClass: Class<TResult>?): DistinctObservable<TResult> {
        TODO("not implemented")
    }

    override fun <TResult : Any?> distinct(fieldName: String?, filter: Bson?, resultClass: Class<TResult>?): DistinctObservable<TResult> {
        TODO("not implemented")
    }

    override fun bulkWrite(requests: MutableList<out WriteModel<out Document>>?): Observable<BulkWriteResult> {
        TODO("not implemented")
    }

    override fun bulkWrite(requests: MutableList<out WriteModel<out Document>>?, options: BulkWriteOptions?): Observable<BulkWriteResult> {
        TODO("not implemented")
    }

    override fun findOneAndDelete(filter: Bson?): Observable<Document> {
        TODO("not implemented")
    }

    override fun findOneAndDelete(filter: Bson?, options: FindOneAndDeleteOptions?): Observable<Document> {
        TODO("not implemented")
    }

    override fun getWriteConcern(): WriteConcern {
        TODO("not implemented")
    }

    override fun getReadConcern(): ReadConcern {
        TODO("not implemented")
    }

    override fun getReadPreference(): ReadPreference {
        TODO("not implemented")
    }
}

class FindObservableImpl(private val list: List<Document>) : FindObservable<Document> {
    override fun partial(partial: Boolean): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun cursorType(cursorType: CursorType?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun showRecordId(showRecordId: Boolean): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun projection(projection: Bson?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun comment(comment: String?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun filter(filter: Bson?): FindObservable<Document> {
        val filterDoc = filter as Document
        return FindObservableImpl.of(list.filter { it == filterDoc })
    }

    override fun first(): Observable<Document> {
        TODO("not implemented")
    }

    override fun collation(collation: Collation?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun min(min: Bson?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun modifiers(modifiers: Bson?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun limit(limit: Int): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun snapshot(snapshot: Boolean): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun skip(skip: Int): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun maxAwaitTime(maxAwaitTime: Long, timeUnit: TimeUnit?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun sort(sort: Bson?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun noCursorTimeout(noCursorTimeout: Boolean): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun max(max: Bson?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun toObservable(): Observable<Document> {
        return if (list.isEmpty()) Observable.empty() else Observable.just(list[0])
    }

    override fun hint(hint: Bson?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun maxTime(maxTime: Long, timeUnit: TimeUnit?): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun subscribe(subscriber: Subscriber<in Document>?): Subscription {
        TODO("not implemented")
    }

    override fun oplogReplay(oplogReplay: Boolean): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun maxScan(maxScan: Long): FindObservable<Document> {
        TODO("not implemented")
    }

    override fun returnKey(returnKey: Boolean): FindObservable<Document> {
        TODO("not implemented")
    }

    companion object {
        fun empty() = FindObservableImpl(emptyList())
        fun of(doc: Document) = FindObservableImpl(listOf(doc))
        fun of(docs: List<Document>) = FindObservableImpl(docs)
    }
}