package mysql;

import java.util.logging.Logger;

import mysql.result.ResultAccount;
import mysql.result.ResultCategory;
import mysql.result.ResultLocation;

/**
 * class to modify the database restricted
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBgui extends DBConnection implements DBIGUI {

    /**
     * configure the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public DBgui(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public ResultCategory[] getCategories() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultCategory[] getCategories(String search) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultLocation[] getLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultLocation[] getLocations(String search) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getAccountId(String accountName) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ResultAccount[] getData(int[] categoryIds, int countryIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultAccount[] getAccounts() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultAccount[] getAccounts(String search) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean addAccount() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setCategory(int accountId, int categoryId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setLocation(int accountId, int locationId) {
        // TODO Auto-generated method stub
        return false;
    }

}
