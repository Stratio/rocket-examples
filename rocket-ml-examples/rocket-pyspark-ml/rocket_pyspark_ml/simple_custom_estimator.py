from pyspark.ml.util import DefaultParamsReadable, DefaultParamsWritable
from pyspark import keyword_only
from pyspark.ml.pipeline import Estimator, Model, Pipeline
from pyspark.ml.param.shared import *
from pyspark.sql.functions import *


class HasMean(Params):
    mean = Param(Params._dummy(), "mean", "mean", typeConverter=TypeConverters.toFloat)

    def __init__(self):
        super(HasMean, self).__init__()

    def setMean(self, value):
        return self._set(mean=value)

    def getMean(self):
        return self.getOrDefault(self.mean)


class HasStandardDeviation(Params):
    standardDeviation = Param(
        Params._dummy(), "standardDeviation", "standardDeviation", typeConverter=TypeConverters.toFloat)

    def __init__(self):
        super(HasStandardDeviation, self).__init__()

    def setStddev(self, value):
        return self._set(standardDeviation=value)

    def getStddev(self):
        return self.getOrDefault(self.standardDeviation)


class HasCenteredThreshold(Params):
    centeredThreshold = Param(Params._dummy(),
                              "centeredThreshold", "centeredThreshold",
                              typeConverter=TypeConverters.toFloat)

    def __init__(self):
        super(HasCenteredThreshold, self).__init__()

    def setCenteredThreshold(self, value):
        return self._set(centeredThreshold=value)

    def getCenteredThreshold(self):
        return self.getOrDefault(self.centeredThreshold)


class NormalDeviation(
    Estimator, HasInputCol, HasPredictionCol, HasCenteredThreshold, DefaultParamsReadable, DefaultParamsWritable
):

    @keyword_only
    def __init__(self, inputCol=None, predictionCol=None, centeredThreshold=1.0):
        super(NormalDeviation, self).__init__()
        kwargs = self._input_kwargs
        self.setParams(**kwargs)

    # Required in Spark >= 3.0
    def setInputCol(self, value):
        """
        Sets the value of :py:attr:`inputCol`.
        """
        return self._set(inputCol=value)

    # Required in Spark >= 3.0
    def setPredictionCol(self, value):
        """
        Sets the value of :py:attr:`predictionCol`.
        """
        return self._set(predictionCol=value)

    @keyword_only
    def setParams(self, inputCol=None, predictionCol=None, centeredThreshold=1.0):
        kwargs = self._input_kwargs
        return self._set(**kwargs)

    def _fit(self, dataset):
        c = self.getInputCol()
        mu, sigma = dataset.agg(avg(c), stddev_samp(c)).first()
        return NormalDeviationModel(
            inputCol=c, mean=mu, standardDeviation=sigma,
            centeredThreshold=self.getCenteredThreshold(),
            predictionCol=self.getPredictionCol())


class NormalDeviationModel(Model, HasInputCol, HasPredictionCol,
                           HasMean, HasStandardDeviation, HasCenteredThreshold,
                           DefaultParamsReadable, DefaultParamsWritable):

    @keyword_only
    def __init__(self, inputCol=None, predictionCol=None, mean=None, standardDeviation=None, centeredThreshold=None):
        super(NormalDeviationModel, self).__init__()
        kwargs = self._input_kwargs
        self.setParams(**kwargs)

    @keyword_only
    def setParams(self, inputCol=None, predictionCol=None, mean=None, standardDeviation=None, centeredThreshold=None):
        kwargs = self._input_kwargs
        return self._set(**kwargs)

    def _transform(self, dataset):
        x = self.getInputCol()
        y = self.getPredictionCol()
        threshold = self.getCenteredThreshold()
        mu = self.getMean()
        sigma = self.getStddev()

        return dataset.withColumn(y, (dataset[x] - mu) > threshold * sigma)
