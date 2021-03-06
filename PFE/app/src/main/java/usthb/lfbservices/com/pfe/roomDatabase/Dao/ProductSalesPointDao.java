package usthb.lfbservices.com.pfe.roomDatabase.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import usthb.lfbservices.com.pfe.models.ProductSalesPoint;

@Dao
public interface ProductSalesPointDao {


    @Query("SELECT * FROM ProductSalesPoint WHERE productBarcode = :productBarcode")
    List<ProductSalesPoint> getAll(String productBarcode);

    @Query("DELETE FROM ProductSalesPoint WHERE productBarcode = :productBarcode AND salesPointId = :salespointId")
    void deleteById(String productBarcode, String salespointId);

    @Query("DELETE FROM ProductSalesPoint WHERE productBarcode = :productBarcode ")
    void deleteByProductBarcode(String productBarcode);

    @Query("SELECT EXISTS (SELECT 1 FROM ProductSalesPoint WHERE productBarcode = :productBarcode AND salesPointId = :salesPointId)")
    boolean exists(String productBarcode, String salesPointId);

    @Update
    void update(ProductSalesPoint productSalesPoint);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ProductSalesPoint... productSalesPoints);
}


