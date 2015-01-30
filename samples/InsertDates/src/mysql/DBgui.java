package mysql;

import java.util.logging.Logger;

import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;

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
    public Category[] getCategories() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category[] getCategories(String search) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location[] getLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location[] getLocations(String search) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getAccountId(String accountName) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Account[] getData(int[] categoryIds, int countryIds) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Account[] getAccounts() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Account[] getAccounts(String search) {
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
